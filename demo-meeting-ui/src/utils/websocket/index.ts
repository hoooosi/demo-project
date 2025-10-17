import type SimplePeer from 'simple-peer'
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

    private initWorker() {
        try {
            this.worker = new Worker(
                new URL('./websocket.worker.ts', import.meta.url),
                { type: 'module' }
            )

            this.worker.addEventListener('message', this.handleWorkerMessage.bind(this))
            this.worker.addEventListener('error', (error) => console.error('[WebSocketManager] Worker error:', error))
        } catch (error) {
            console.error('[WebSocketManager] Failed to create worker:', error)
        }
    }

    private handleWorkerMessage(event: MessageEvent<WorkerMessage>) {
        const { type, payload } = event.data

        switch (type) {
            case WorkerMessageType.CONNECTED:
                this.isConnected = true
                this.notifyConnectionListeners(true)
                console.log('[WebSocketManager] Connected to WebSocket')
                // Resolve connection Promise
                if (this.connectionResolve) {
                    this.connectionResolve()
                    this.connectionResolve = null
                    this.connectionReject = null
                }
                break

            case WorkerMessageType.DISCONNECTED:
                this.isConnected = false
                this.notifyConnectionListeners(false)
                // If disconnected, reject connection Promise
                if (this.connectionReject) {
                    this.connectionReject(new Error('WebSocket disconnected'))
                    this.connectionResolve = null
                    this.connectionReject = null
                }
                break

            case WorkerMessageType.ERROR:
                // If connection error, reject Promise
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

    // Handle incoming WSMessage
    private handleMessage(message: WSMessage) {
        const { messageType } = message

        // Notify specific type listeners
        const typeListeners = this.listeners.get(`type:${messageType}`)
        if (typeListeners) {
            typeListeners.forEach((listener) => listener(message))
        }

        // Notify all message listeners
        const allListeners = this.listeners.get('all')
        if (allListeners) {
            allListeners.forEach((listener) => listener(message))
        }
    }

    // Notify connection status listeners
    private notifyConnectionListeners(connected: boolean) {
        this.connectionListeners.forEach((listener) => listener(connected))
    }

    // Connect WebSocket
    connect(config: Omit<WSConnectConfig, 'url'> & { hostname?: string; port?: number }) {
        // If already connected or connecting, return existing Promise
        if (this.isConnected)
            return Promise.resolve()

        // If connecting, return existing Promise
        if (this.connectionPromise)
            return this.connectionPromise

        // Ensure worker is initialized
        if (!this.worker)
            return Promise.reject(new Error('Worker not initialized'))

        const { hostname = window.location.hostname, port = 8089, token } = config

        this.config = {
            url: `ws://${hostname}:${port}/ws?token=${token}`,
            token,
            reconnect: config.reconnect ?? true,
            reconnectInterval: config.reconnectInterval ?? 3000,
            heartbeatInterval: config.heartbeatInterval ?? 5000,
        }

        // Create new connection Promise
        this.connectionPromise = new Promise<void>((resolve, reject) => {
            this.connectionResolve = resolve
            this.connectionReject = reject
            // Set timeout (30 seconds)
            setTimeout(() => {
                if (this.connectionReject) {
                    this.connectionReject(new Error('WebSocket connection timeout'))
                    this.connectionResolve = null
                    this.connectionReject = null
                    this.connectionPromise = null
                }
            }, 30000)
        })

        this.worker.postMessage({
            type: WorkerMessageType.CONNECT,
            payload: this.config,
        } as WorkerMessage)

        return this.connectionPromise
    }

    // Wait for connection to be established
    async waitForConnection(timeout: number = 30000): Promise<void> {
        if (this.isConnected)
            return Promise.resolve()

        if (this.connectionPromise)
            return this.connectionPromise

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

    // Check connection state
    getConnectionState(): boolean {
        return this.isConnected
    }

    // Disconnect WebSocket
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

    // Send message
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

    // Listen for specific message types
    on(messageType: MessageType | 'all', callback: (data: any) => void) {
        const key = messageType === 'all' ? 'all' : `type:${messageType}`

        if (!this.listeners.has(key)) {
            this.listeners.set(key, new Set())
        }

        this.listeners.get(key)!.add(callback)

        // Return unsubscribe function
        return () => this.listeners.get(key)?.delete(callback)
    }

    // Listen for connection status changes
    onConnectionChange(callback: (connected: boolean) => void) {
        this.connectionListeners.add(callback)

        return () => this.connectionListeners.delete(callback)
    }

    // Destroy Manager
    destroy() {
        this.disconnect()

        if (this.worker) {
            this.worker.terminate()
            this.worker = null
        }

        this.listeners.clear()
        this.connectionListeners.clear()
    }

    sendChat(text: string, targetType: MessageTargetType, receiverId: string) {
        this.send({
            messageType: MessageType.TEXT,
            messageTargetType: targetType,
            receiverId,
            content: text,
        })
    }

    sendSignal(receiverId: string, signal: SimplePeer.SignalData) {
        this.send({
            messageType: MessageType.SIGNAL,
            messageTargetType: MessageTargetType.SINGLE,
            receiverId,
            content: signal,
        })
    }
}

// Export singleton
export const wsManager = new WebSocketManager()

// Export types
export { MessageType, MessageTargetType } from './websocket.types'
export type { WSMessage } from './websocket.types'
