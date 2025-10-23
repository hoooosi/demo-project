/**
 * WebRTC Configuration
 * Configure ICE servers including STUN and TURN (COTURN) servers
 */

export interface RTCConfig {
    iceServers: RTCIceServer[]
}

/**
 * Default COTURN configuration
 * Update these values according to your COTURN server setup
 */
export const defaultRTCConfig: RTCConfig = {
    iceServers: [
        // Public STUN server for NAT traversal
        {
            urls: 'stun:stun.l.google.com:19302'
        },
        // Your COTURN server for relay (TURN)
        // Supports both UDP and TCP transport
        {
            urls: [
                'turn:192.168.31.3:3478?transport=udp',
                'turn:192.168.31.3:3478?transport=tcp'
            ],
            username: 'coturn',
            credential: 'coturn'
        }
    ]
}

/**
 * Get RTC configuration with optional custom settings
 */
export function getRTCConfig(customConfig?: Partial<RTCConfig>): RTCConfig {
    return {
        ...defaultRTCConfig,
        ...customConfig
    }
}
