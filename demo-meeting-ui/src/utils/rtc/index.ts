import { reactive } from 'vue'
import { wsManager, MessageType } from '@/utils/websocket'
import type { WSMessage } from '@/utils/websocket'
import SimplePeer from 'simple-peer'
import { defaultRTCConfig } from '@/utils/rtc/rtc.config'

export interface Participant {
    userId: string
    peer: SimplePeer.Instance
    stream?: MediaStream
}

export function useWebRTC() {
    let currentStream: MediaStream | undefined
    const localMap = reactive(new Map<string, Participant>())

    // Use ICE servers from config (includes COTURN for NAT traversal)
    const { iceServers } = defaultRTCConfig

    const SIGNAL_LISTENER = wsManager.on(MessageType.SIGNAL, async (msg: WSMessage) => {
        const { senderId, content } = msg
        const peer = localMap.get(senderId)?.peer || createNewPeer({
            initiator: false,
            trickle: true,
            config: { iceServers }
        }, senderId)
        peer.signal(content)
    })

    const createNewPeer = (options: SimplePeer.Options, userId: string) => {
        const peer = new SimplePeer(options)
            .on('signal', data => {
                wsManager.sendSignal(userId, data)
            })
            .on('connect', () => {
                if (currentStream) {
                    peer.addStream(currentStream)
                }
            })
            .on('data', (data: Buffer) => {
                console.log("Received data:", data.toString())
            })
            .on('error', err => {
                console.error(`Peer ${userId} connection error:`, err)
            })
            .on('stream', (stream) => {
                console.log(`Received stream from ${userId}`)
                const participant = localMap.get(userId)
                if (participant) {
                    participant.stream = stream
                }
            })
            .on('close', () => {
                console.log(`Peer ${userId} connection closed`)
                localMap.delete(userId)
            })

        localMap.set(userId, { userId, peer })
        return peer
    }

    const resetStream = (stream: MediaStream) => {
        if (stream === currentStream) return
        localMap.forEach(({ peer }) => {
            try {
                if (currentStream)
                    peer.removeStream(currentStream)
                peer.addStream(stream)
            } catch (error) { }
        })
        currentStream = stream
    }

    const createRTC = async (userId: string) => {
        createNewPeer({
            initiator: true,
            trickle: true,
            config: { iceServers }
        }, userId)
    }

    return {
        localMap,
        cleanup: () => {
            SIGNAL_LISTENER()
            // Stop current stream tracks
            if (currentStream) {
                currentStream.getTracks().forEach((track) => track.stop())
            }
            // Destroy all peer connections
            localMap.forEach(({ peer }) => peer.destroy())
            localMap.clear()
        },
        setStream: resetStream,
        createRTC,
    }
}
