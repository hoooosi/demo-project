// WebSocket Worker Message Types
export enum WorkerMessageType {
    // Worker -> Main
    CONNECTED = 'connected',
    DISCONNECTED = 'disconnected',
    ERROR = 'error',
    MESSAGE = 'message',

    // Main -> Worker
    CONNECT = 'connect',
    DISCONNECT = 'disconnect',
    SEND = 'send',
}

// Message target types (corresponding to backend)
export enum MessageTargetType {
    SINGLE = 1,
    MEETING = 2
}

// Message types (corresponding to backend)
export enum MessageType {
    TEXT = 1,
    IMAGE = 2,
    FILE = 3,
    AUDIO = 4,
    VIDEO = 5,
    EVENT = 50,

    SIGNAL = 98,
}

// Worker message interface
export interface WorkerMessage {
    type: WorkerMessageType
    payload?: any
}

// WebSocket message interface
export interface WSMessage {
    messageType: MessageType
    messageTargetType: MessageTargetType
    senderId: string
    receiverId: string
    content: any
    sendTime: string
}

// Connection configuration
export interface WSConnectConfig {
    url: string
    token: string
    reconnect?: boolean
    reconnectInterval?: number
    heartbeatInterval?: number
}

// Connection state
export enum ConnectionState {
    CONNECTING = 'connecting',
    CONNECTED = 'connected',
    DISCONNECTING = 'disconnecting',
    DISCONNECTED = 'disconnected',
    RECONNECTING = 'reconnecting',
}
