import { type Ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { wsManager, MessageType, MessageTargetType } from '@/utils/websocket'
import type { WSMessage } from '@/utils/websocket'

export interface Participant {
    userId: string
    nickName?: string
    RTC: RTCPeerConnection
    stream?: MediaStream
}

export function useWebRTC(streamRef: Ref<MediaStream>) {
    const localStream = streamRef
    const localMap = reactive(new Map<string, Participant>())
    const unsubscribeHandlers: (() => void)[] = []    // ICE 服务器配置
    const iceServers = [
        { urls: 'stun:stun.l.google.com:19302' },
        { urls: 'stun:stun1.l.google.com:19302' },
    ]
    const create = (userId: string) => {
        const RTC = new RTCPeerConnection({ iceServers })

        // 发送 ICE candidate
        RTC.onicecandidate = (event) => {
            if (event.candidate) {
                wsManager.send({
                    messageType: MessageType.ICE_CANDIDATE,
                    messageTargetType: MessageTargetType.SINGLE,
                    receiverId: userId,
                    content: event.candidate.toJSON(),
                })
            }
        }

        // 接收远程媒体流
        RTC.ontrack = (event) => {
            const participant = localMap.get(userId)
            if (participant && event.streams[0] !== participant.stream) {
                ElMessage.success(`收到用户 ${userId} 的媒体流`)
                participant.stream = event.streams[0]
            }
        }

        // 连接状态监控
        RTC.onconnectionstatechange = () => {

        }

        // 处理 SDP 协商
        RTC.onnegotiationneeded = async () => {
            const offer = await RTC.createOffer()
            await RTC.setLocalDescription(offer);
            wsManager.sendOffer(userId, offer)
        }

        return RTC
    }

    const offerHandler = async (msg: WSMessage) => {
        try {
            const { senderId, content } = msg
            if (!senderId) return

            const RTC = create(senderId)
            localStream.value.getTracks().forEach((track) => {
                RTC.addTrack(track, localStream.value)
            })
            await RTC.setRemoteDescription(new RTCSessionDescription(content))

            const answer = await RTC.createAnswer()
            await RTC.setLocalDescription(answer)
            wsManager.sendAnswer(senderId, answer)

            localMap.set(senderId, { userId: senderId, RTC })
        } catch (error) {
            console.error('Error handling offer:', error)
            ElMessage.error('处理连接请求失败')
        }
    }

    const answerHandler = async (msg: WSMessage) => {
        try {
            const { senderId, content } = msg
            if (!senderId) return
            const participant = localMap.get(senderId)
            if (participant?.RTC)
                await participant.RTC.setRemoteDescription(new RTCSessionDescription(content))
        } catch (error) {
            console.error('Error handling answer:', error)
            ElMessage.error('处理连接应答失败')
        }
    }

    const iceCandidateHandler = async (msg: WSMessage) => {
        try {
            const { senderId, content } = msg
            if (!senderId) return

            const participant = localMap.get(senderId)
            if (participant?.RTC) {
                await participant.RTC.addIceCandidate(new RTCIceCandidate(content))
            }
        } catch (error) {
            console.error('Error handling ICE candidate:', error)
        }
    }

    const createRTC = async (userId: string) => {
        try {
            const RTC = create(userId)
            localStream.value.getTracks().forEach((track) => {
                RTC.addTrack(track, localStream.value)
            })

            const offer = await RTC.createOffer()
            await RTC.setLocalDescription(offer)
            wsManager.sendOffer(userId, offer)
            localMap.set(userId, { userId, RTC })
        } catch (error) {
            console.error('Error creating peer connection:', error)
            ElMessage.error('创建连接失败')
        }
    }

    const registerMessageHandlers = () => {
        unsubscribeHandlers.push(
            wsManager.on(MessageType.OFFER, offerHandler),
            wsManager.on(MessageType.ANSWER, answerHandler),
            wsManager.on(MessageType.ICE_CANDIDATE, iceCandidateHandler)
        )
    }


    const unregisterMessageHandlers = () => {
        unsubscribeHandlers.forEach((unsubscribe) => unsubscribe())
        unsubscribeHandlers.length = 0
    }


    const closeAllConnections = () => {
        localMap.forEach((participant) => participant.RTC.close())
        localMap.clear()
    }

    return {
        localMap,
        createPeerConnection: createRTC,
        registerMessageHandlers,
        unregisterMessageHandlers,
        closeAllConnections
    }
}
