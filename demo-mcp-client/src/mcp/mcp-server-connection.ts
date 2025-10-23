import { Client } from '@modelcontextprotocol/sdk/client/index.js';
import { StdioClientTransport } from '@modelcontextprotocol/sdk/client/stdio.js';
import type { MCPServerConfig, MCPTool, ToolCallResult, ServerInfo } from '../types';

/**
 * Single MCP Server Connection
 * Manages connection to one MCP server
 */
export class MCPServerConnection {
    private mcpClient: Client;
    private transport: StdioClientTransport;
    private connected: boolean = false;
    public readonly serverName: string;
    private serverInfo: ServerInfo | null = null;

    constructor(serverConfig: MCPServerConfig, serverName: string) {
        this.serverName = serverName;

        // Initialize MCP client
        this.mcpClient = new Client(
            {
                name: 'mcp-openai-client',
                version: '1.0.0',
            },
            {
                capabilities: {
                    sampling: {},
                },
            }
        );

        // Initialize transport
        this.transport = new StdioClientTransport({
            command: serverConfig.command,
            args: serverConfig.args,
            env: serverConfig.env,
        });
    }

    /**
     * Connect to the MCP server
     */
    async connect(): Promise<void> {
        if (this.connected) {
            console.log(`⚠️  Server "${this.serverName}" is already connected`);
            return;
        }

        try {
            console.log(`🔌 Connecting to MCP server: ${this.serverName}...`);
            await this.mcpClient.connect(this.transport);
            this.connected = true;

            const serverVersion = this.mcpClient.getServerVersion();
            this.serverInfo = {
                name: serverVersion?.name || this.serverName,
                version: serverVersion?.version,
            };

            console.log(`✅ Connected to ${this.serverName} (${this.serverInfo.name} v${this.serverInfo.version})`);
        } catch (error: any) {
            console.log(`❌ Failed to connect to server "${this.serverName}":`, error.message);
            throw error;
        }
    }

    /**
     * List all available tools from this server
     */
    async listTools(): Promise<MCPTool[]> {
        if (!this.connected) {
            throw new Error(`Server "${this.serverName}" is not connected`);
        }

        try {
            const response = await this.mcpClient.listTools();
            return response.tools || [];
        } catch (error) {
            console.error(`❌ Failed to list tools from server "${this.serverName}":`, error);
            return [];
        }
    }

    /**
     * Call a tool on this server
     */
    async callTool(toolName: string, args: Record<string, unknown>): Promise<ToolCallResult> {
        if (!this.connected) {
            throw new Error(`Server "${this.serverName}" is not connected`);
        }

        console.log(`🔧 [${this.serverName}] Calling tool: ${toolName}`, JSON.stringify(args));

        try {
            const response = await this.mcpClient.callTool({
                name: toolName,
                arguments: args,
            });
            return response;
        } catch (error) {
            console.error(`❌ Tool call failed on server "${this.serverName}":`, error);
            throw error;
        }
    }

    /**
     * Disconnect from the server
     */
    async disconnect(): Promise<void> {
        if (!this.connected) {
            return;
        }

        try {
            console.log(`👋 Disconnecting from ${this.serverName}...`);
            await this.transport.close();
            this.connected = false;
        } catch (error) {
            console.error(`❌ Error disconnecting from "${this.serverName}":`, error);
        }
    }

    /**
     * Check if connected
     */
    isConnected(): boolean {
        return this.connected;
    }

    /**
     * Get server info
     */
    getServerInfo(): ServerInfo | null {
        return this.serverInfo;
    }
}
