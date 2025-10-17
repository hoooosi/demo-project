import type { WorkerMessage, WSConnectConfig, WSMessage } from './websocket.types'
import { WorkerMessageType, ConnectionState } from './websocket.types'

let ws: WebSocket | null = null
let config: WSConnectConfig | null = null
let reconnectTimer: number | null = null
let heartbeatTimer: number | null = null
let connectionState: ConnectionState = ConnectionState.DISCONNECTED

// Send message to main thread
const postMessageToMain = (type: WorkerMessageType, payload?: any) => {
    self.postMessage({ type, payload } as WorkerMessage)
}

// Connect to WebSocket
const connect = (connectConfig: WSConnectConfig) => {
    try {
        config = connectConfig
        if (ws && (ws.readyState === WebSocket.CONNECTING || ws.readyState === WebSocket.OPEN))
            return
        connectionState = ConnectionState.CONNECTING

        // Create WebSocket
        ws = new WebSocket(config.url)
        ws.onopen = () => {
            connectionState = ConnectionState.CONNECTED
            postMessageToMain(WorkerMessageType.CONNECTED)

            // Clear reconnect timer
            if (reconnectTimer) {
                clearTimeout(reconnectTimer)
                reconnectTimer = null
            }

            // Start heartbeat
            startHeartbeat()
        }

        // Handle incoming messages
        ws.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data)
                postMessageToMain(WorkerMessageType.MESSAGE, message)
            } catch (error) {
                console.error('[Worker] Failed to parse message:', error)
            }
        }

        // Handle errors and close events
        ws.onerror = (error) => {
            console.error('[Worker] WebSocket error:', error)
            postMessageToMain(WorkerMessageType.ERROR, { message: 'WebSocket error' })
        }

        // Handle close events
        ws.onclose = (event) => {
            console.log('[Worker] WebSocket closed:', event.code, event.reason)
            connectionState = ConnectionState.DISCONNECTED
            postMessageToMain(WorkerMessageType.DISCONNECTED, {
                code: event.code,
                reason: event.reason,
            })

            // Stop heartbeat
            stopHeartbeat()

            // Attempt reconnect
            if (config?.reconnect && !event.wasClean)
                attemptReconnect()
        }
    } catch (error) {
        postMessageToMain(WorkerMessageType.ERROR, { message: 'Failed to create WebSocket' })

        if (config?.reconnect)
            attemptReconnect()
    }
}

// Disconnect
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

// Send message
const send = (message: Omit<WSMessage, 'senderId' | 'sendTime'>) => {

    try {
        if (!ws || ws.readyState !== WebSocket.OPEN) {
            console.error('[Worker] WebSocket is not connected')
            postMessageToMain(WorkerMessageType.ERROR, { message: 'WebSocket is not connected' })
            return
        }
        ws.send(JSON.stringify(message))
    } catch (error) {
        console.error('[Worker] Failed to send message:', error)
        postMessageToMain(WorkerMessageType.ERROR, { message: 'Failed to send message' })
    }
}

// Attempt reconnect
const attemptReconnect = () => {
    if (connectionState === ConnectionState.RECONNECTING)
        return

    connectionState = ConnectionState.RECONNECTING
    const interval = config?.reconnectInterval || 3000
    reconnectTimer = self.setTimeout(() => {
        if (config) {
            connect(config)
        }
    }, interval)
}

// Start heartbeat
const startHeartbeat = () => {
    if (!config?.heartbeatInterval)
        return
    stopHeartbeat()
    heartbeatTimer = self.setInterval(() => {
        if (ws && ws.readyState === WebSocket.OPEN)
            ws.send(JSON.stringify({ messageType: 99 }))
    }, config.heartbeatInterval)
}

// Stop heartbeat
const stopHeartbeat = () => {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer)
        heartbeatTimer = null
    }
}

// Listen for messages from the main thread
self.addEventListener('message', (event: MessageEvent<WorkerMessage>) => {
    const { type, payload } = event.data

    switch (type) {
        case WorkerMessageType.CONNECT:
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


export type { }
