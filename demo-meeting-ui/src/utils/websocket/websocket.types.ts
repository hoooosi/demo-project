// WebSocket Worker 消息类型
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

// 消息目标类型（对应后端）
export enum MessageTargetType {
    SINGLE = 1,
    MEETING = 2
}

// 消息类型（对应后端）
export enum MessageType {
    TEXT = 1,
    IMAGE = 2,
    FILE = 3,
    AUDIO = 4,
    VIDEO = 5,
    OFFER = 11,
    ANSWER = 12,
    ICE_CANDIDATE = 13
}

// Worker 消息接口
export interface WorkerMessage {
    type: WorkerMessageType
    payload?: any
}

// WebSocket 消息接口
export interface WSMessage {
    messageType: MessageType
    messageTargetType: MessageTargetType
    senderId?: string
    receiverId?: string
    content: any
    sendTime?: string
}

// 连接配置
export interface WSConnectConfig {
    url: string
    token: string
    reconnect?: boolean
    reconnectInterval?: number
    heartbeatInterval?: number
}

// 连接状态
export enum ConnectionState {
    CONNECTING = 'connecting',
    CONNECTED = 'connected',
    DISCONNECTING = 'disconnecting',
    DISCONNECTED = 'disconnected',
    RECONNECTING = 'reconnecting',
}
