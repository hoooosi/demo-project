/**
 * Type definitions for MCP Client
 */

/**
 * MCP Server Configuration Interface
 */
export interface MCPServerConfig {
    type: 'stdio';
    command: string;
    args: string[];
    env?: Record<string, string>;
}

/**
 * Configuration file structure
 */
export interface MCPConfig {
    servers: Record<string, MCPServerConfig>;
}

/**
 * MCP Tool definition
 */
export interface MCPTool {
    name: string;
    description?: string;
    inputSchema: any;
}

/**
 * Tool call result
 */
export interface ToolCallResult {
    content?: any[];
    isError?: boolean;
    [key: string]: any;
}

/**
 * Server connection info
 */
export interface ServerInfo {
    name: string;
    version?: string;
    capabilities?: any;
}
