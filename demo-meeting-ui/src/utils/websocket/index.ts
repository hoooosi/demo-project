import type { WorkerMessage, WSConnectConfig, WSMessage } from './websocket.types'
import { WorkerMessageType, MessageType, MessageTargetType } from './websocket.types'

export class WebSocketManager {
    private worker: Worker | null = null
    private listeners: Map<string, Set<(data: any) => void>> = new Map()
    private connectionListeners: Set<(connected: boolean) => void> = new Set()
    private config: WSConnectConfig | null = null
    private isConnected: boolean = false
    private connectionPromise: Promise<void> | null = null
    private connectionResolve: (() => void) | null = null
    private connectionReject: ((error: any) => void) | null = null

    constructor() {
        this.initWorker()
    }
    // 初始化 Worker
    private initWorker() {
        try {
            // 使用 Vite 的 ?worker 后缀导入
            this.worker = new Worker(
                new URL('./websocket.worker.ts', import.meta.url),
                { type: 'module' }
            )

            this.worker.addEventListener('message', this.handleWorkerMessage.bind(this))
            this.worker.addEventListener('error', (error) => {
                console.error('[WebSocketManager] Worker error:', error)
            })
        } catch (error) {
            console.error('[WebSocketManager] Failed to create worker:', error)
        }
    }    // 处理 Worker 消息
    private handleWorkerMessage(event: MessageEvent<WorkerMessage>) {
        const { type, payload } = event.data

        switch (type) {
            case WorkerMessageType.CONNECTED:
                this.isConnected = true
                this.notifyConnectionListeners(true)
                // 解决连接 Promise
                if (this.connectionResolve) {
                    this.connectionResolve()
                    this.connectionResolve = null
                    this.connectionReject = null
                }
                break

            case WorkerMessageType.DISCONNECTED:
                this.isConnected = false
                this.notifyConnectionListeners(false)
                // 如果连接失败，拒绝 Promise
                if (this.connectionReject) {
                    this.connectionReject(new Error('WebSocket disconnected'))
                    this.connectionResolve = null
                    this.connectionReject = null
                }
                break

            case WorkerMessageType.ERROR:
                console.error('[WebSocketManager] Error from worker:', payload)
                // 连接错误时拒绝 Promise
                if (this.connectionReject) {
                    this.connectionReject(payload)
                    this.connectionResolve = null
                    this.connectionReject = null
                }
                break

            case WorkerMessageType.MESSAGE:
                this.handleMessage(payload)
                break
        }
    }

    // 处理接收到的消息
    private handleMessage(message: WSMessage) {
        const { messageType } = message

        // 通知特定类型的监听器
        const typeListeners = this.listeners.get(`type:${messageType}`)
        if (typeListeners) {
            typeListeners.forEach((listener) => listener(message))
        }

        // 通知所有消息监听器
        const allListeners = this.listeners.get('all')
        if (allListeners) {
            allListeners.forEach((listener) => listener(message))
        }
    }

    // 通知连接状态监听器
    private notifyConnectionListeners(connected: boolean) {
        this.connectionListeners.forEach((listener) => listener(connected))
    }    // 连接 WebSocket
    connect(config: Omit<WSConnectConfig, 'url'> & { hostname?: string; port?: number }) {
        // 如果已经连接或正在连接，返回现有的 Promise
        if (this.isConnected) {
            console.log('[WebSocketManager] Already connected')
            return Promise.resolve()
        }

        if (this.connectionPromise) {
            console.log('[WebSocketManager] Connection in progress')
            return this.connectionPromise
        }

        const { hostname = window.location.hostname, port = 8089, token } = config

        this.config = {
            url: `ws://${hostname}:${port}/ws?token=${token}`,
            token,
            reconnect: config.reconnect ?? true,
            reconnectInterval: config.reconnectInterval ?? 3000,
            heartbeatInterval: config.heartbeatInterval ?? 5000,
        }

        console.log('[WebSocketManager] Initiating connection to:', this.config.url)

        if (!this.worker) {
            console.error('[WebSocketManager] Worker not initialized')
            return Promise.reject(new Error('Worker not initialized'))
        }

        // 创建连接 Promise
        this.connectionPromise = new Promise<void>((resolve, reject) => {
            this.connectionResolve = resolve
            this.connectionReject = reject            // 设置超时（30秒）
            setTimeout(() => {
                if (this.connectionReject) {
                    console.error('[WebSocketManager] Connection timeout')
                    this.connectionReject(new Error('WebSocket connection timeout'))
                    this.connectionResolve = null
                    this.connectionReject = null
                    this.connectionPromise = null
                }
            }, 30000)
        })

        console.log('[WebSocketManager] Sending CONNECT message to worker')
        this.worker.postMessage({
            type: WorkerMessageType.CONNECT,
            payload: this.config,
        } as WorkerMessage)

        return this.connectionPromise
    }

    // 等待连接完成
    async waitForConnection(timeout: number = 30000): Promise<void> {
        if (this.isConnected) {
            return Promise.resolve()
        }

        if (this.connectionPromise) {
            return this.connectionPromise
        }

        return Promise.race([
            new Promise<void>((resolve) => {
                const unsubscribe = this.onConnectionChange((connected) => {
                    if (connected) {
                        unsubscribe()
                        resolve()
                    }
                })
            }),
            new Promise<void>((_, reject) => {
                setTimeout(() => reject(new Error('Connection timeout')), timeout)
            })
        ])
    }

    // 检查是否已连接
    getConnectionState(): boolean {
        return this.isConnected
    }    // 断开连接
    disconnect() {
        if (!this.worker) {
            return
        }

        this.isConnected = false
        this.connectionPromise = null
        this.connectionResolve = null
        this.connectionReject = null

        this.worker.postMessage({
            type: WorkerMessageType.DISCONNECT,
        } as WorkerMessage)
    }

    // 发送消息
    send(message: Omit<WSMessage, 'senderId' | 'sendTime'>) {
        if (!this.worker) {
            console.error('[WebSocketManager] Worker not initialized')
            return
        }

        this.worker.postMessage({
            type: WorkerMessageType.SEND,
            payload: message,
        } as WorkerMessage)
    }

    // 监听特定类型的消息
    on(messageType: MessageType | 'all', callback: (data: any) => void) {
        const key = messageType === 'all' ? 'all' : `type:${messageType}`

        if (!this.listeners.has(key)) {
            this.listeners.set(key, new Set())
        }

        this.listeners.get(key)!.add(callback)

        // 返回取消监听的函数
        return () => {
            this.listeners.get(key)?.delete(callback)
        }
    }

    // 监听连接状态变化
    onConnectionChange(callback: (connected: boolean) => void) {
        this.connectionListeners.add(callback)

        // 返回取消监听的函数
        return () => {
            this.connectionListeners.delete(callback)
        }
    }

    // 销毁 Manager
    destroy() {
        this.disconnect()

        if (this.worker) {
            this.worker.terminate()
            this.worker = null
        }

        this.listeners.clear()
        this.connectionListeners.clear()
    }

    // 便捷方法：发送聊天消息
    sendChat(text: string, targetType: MessageTargetType = MessageTargetType.MEETING, receiverId: string) {
        this.send({
            messageType: MessageType.TEXT,
            messageTargetType: targetType,
            receiverId,
            content: text,
        })
    }

    // 便捷方法：发送用户加入消息
    sendUserJoin(meetingId: string, nickName: string) {
        this.send({
            messageType: MessageType.TEXT,
            messageTargetType: MessageTargetType.MEETING,
            content: { meetingId, nickName },
        })
    }

    // 便捷方法：发送用户离开消息
    sendUserLeave(nickName: string) {
        this.send({
            messageType: MessageType.TEXT,
            messageTargetType: MessageTargetType.MEETING,
            content: { nickName },
        })
    }

    // 便捷方法：发送 WebRTC Offer
    sendOffer(receiverId: string, offer: RTCSessionDescriptionInit) {
        this.send({
            messageType: MessageType.OFFER,
            messageTargetType: MessageTargetType.SINGLE,
            receiverId,
            content: offer,
        })
    }    // 便捷方法：发送 WebRTC Answer
    sendAnswer(receiverId: string, answer: RTCSessionDescriptionInit) {
        this.send({
            messageType: MessageType.ANSWER,
            messageTargetType: MessageTargetType.SINGLE,
            receiverId,
            content: answer,
        })
    }
}

// 导出单例
export const wsManager = new WebSocketManager()

// 导出类型
export { MessageType, MessageTargetType } from './websocket.types'
export type { WSMessage } from './websocket.types'
