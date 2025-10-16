import type { WorkerMessage, WSConnectConfig, WSMessage } from './websocket.types'
import { WorkerMessageType, ConnectionState } from './websocket.types'

let ws: WebSocket | null = null
let config: WSConnectConfig | null = null
let reconnectTimer: number | null = null
let heartbeatTimer: number | null = null
let connectionState: ConnectionState = ConnectionState.DISCONNECTED

// 发送消息给主线程
const postMessageToMain = (type: WorkerMessageType, payload?: any) => {
    self.postMessage({ type, payload } as WorkerMessage)
}

// 连接 WebSocket
const connect = (connectConfig: WSConnectConfig) => {
    console.log('[Worker] Received CONNECT request with config:', connectConfig)
    config = connectConfig

    if (ws && (ws.readyState === WebSocket.CONNECTING || ws.readyState === WebSocket.OPEN)) {
        console.log('[Worker] WebSocket already connecting or connected')
        return
    }

    connectionState = ConnectionState.CONNECTING
    console.log('[Worker] Creating WebSocket connection to:', config.url)

    try {
        ws = new WebSocket(config.url)
        console.log('[Worker] WebSocket instance created, readyState:', ws.readyState)

        ws.onopen = () => {
            console.log('[Worker] WebSocket connected')
            connectionState = ConnectionState.CONNECTED
            postMessageToMain(WorkerMessageType.CONNECTED)

            // 清除重连定时器
            if (reconnectTimer) {
                clearTimeout(reconnectTimer)
                reconnectTimer = null
            }

            // 开启心跳
            startHeartbeat()
        }

        ws.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data)
                postMessageToMain(WorkerMessageType.MESSAGE, message)
            } catch (error) {
                console.error('[Worker] Failed to parse message:', error)
            }
        }

        ws.onerror = (error) => {
            console.error('[Worker] WebSocket error:', error)
            postMessageToMain(WorkerMessageType.ERROR, { message: 'WebSocket error' })
        }

        ws.onclose = (event) => {
            console.log('[Worker] WebSocket closed:', event.code, event.reason)
            connectionState = ConnectionState.DISCONNECTED
            postMessageToMain(WorkerMessageType.DISCONNECTED, {
                code: event.code,
                reason: event.reason,
            })

            // 停止心跳
            stopHeartbeat()

            // 自动重连
            if (config?.reconnect && !event.wasClean) {
                attemptReconnect()
            }
        }
    } catch (error) {
        console.error('[Worker] Failed to create WebSocket:', error)
        postMessageToMain(WorkerMessageType.ERROR, { message: 'Failed to create WebSocket' })

        if (config?.reconnect) {
            attemptReconnect()
        }
    }
}

// 断开连接
const disconnect = () => {
    if (reconnectTimer) {
        clearTimeout(reconnectTimer)
        reconnectTimer = null
    }

    stopHeartbeat()

    if (ws) {
        connectionState = ConnectionState.DISCONNECTING
        ws.close(1000, 'Client disconnect')
        ws = null
    }

    connectionState = ConnectionState.DISCONNECTED
}

// 发送消息
const send = (message: WSMessage) => {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
        console.error('[Worker] WebSocket is not connected')
        postMessageToMain(WorkerMessageType.ERROR, { message: 'WebSocket is not connected' })
        return
    }

    try {
        // 添加发送者 ID 和时间戳
        const fullMessage: WSMessage = {
            ...message,
            senderId: String(config?.userId),
            sendTime: Date.now(),
        }

        ws.send(JSON.stringify(fullMessage))
    } catch (error) {
        console.error('[Worker] Failed to send message:', error)
        postMessageToMain(WorkerMessageType.ERROR, { message: 'Failed to send message' })
    }
}

// 尝试重连
const attemptReconnect = () => {
    if (connectionState === ConnectionState.RECONNECTING) {
        return
    }

    connectionState = ConnectionState.RECONNECTING

    const interval = config?.reconnectInterval || 3000

    console.log(`[Worker] Attempting to reconnect in ${interval}ms...`)

    reconnectTimer = self.setTimeout(() => {
        if (config) {
            connect(config)
        }
    }, interval)
}

// 启动心跳
const startHeartbeat = () => {
    if (!config?.heartbeatInterval) {
        return
    }

    stopHeartbeat()

    heartbeatTimer = self.setInterval(() => {
        if (ws && ws.readyState === WebSocket.OPEN) {
            // 发送 ping 消息
            try {
                ws.send(JSON.stringify({ messageType: 99 }))
            } catch (error) {
                console.error('[Worker] Failed to send heartbeat:', error)
            }
        }
    }, config.heartbeatInterval)
}

// 停止心跳
const stopHeartbeat = () => {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer)
        heartbeatTimer = null
    }
}

// 监听主线程消息
self.addEventListener('message', (event: MessageEvent<WorkerMessage>) => {
    const { type, payload } = event.data
    console.log('[Worker] Received message from main thread:', type, payload)

    switch (type) {
        case WorkerMessageType.CONNECT:
            console.log('[Worker] Processing CONNECT message')
            connect(payload as WSConnectConfig)
            break

        case WorkerMessageType.DISCONNECT:
            disconnect()
            break

        case WorkerMessageType.SEND:
            send(payload as WSMessage)
            break

        default:
            console.warn('[Worker] Unknown message type:', type)
    }
})

// 导出类型（用于 TypeScript）
export type { }
