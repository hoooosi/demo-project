import type { MCPServerConfig, MCPTool, ToolCallResult } from '../types';
import { MCPServerConnection } from './mcp-server-connection';

/**
 * MCP Manager - Manages multiple MCP server connections
 * Aggregates tools from all servers and routes tool calls
 */
export class MCPManager {
    private servers: Map<string, MCPServerConnection> = new Map();
    private toolToServerMap: Map<string, string> = new Map();
    private allTools: MCPTool[] = [];

    constructor(private mcpConfig: Record<string, MCPServerConfig>) { }

    /**
     * Initialize all servers from config
     */
    async init(): Promise<void> {
        const serverNames = Object.keys(this.mcpConfig);

        if (serverNames.length === 0) {
            console.log('\n⚠️  No servers configured');
            return;
        }

        console.log(`🚀 Initializing ${serverNames.length} MCP server(s)...`);

        // Connect to all servers
        const connectionPromises = serverNames.map(async (serverName) => {
            const serverConfig = this.mcpConfig[serverName];
            if (!serverConfig) {
                console.error(`⚠️  Server config not found for "${serverName}"`);
                return { serverName, success: false };
            }

            const connection = new MCPServerConnection(serverConfig, serverName);

            try {
                await connection.connect();
                this.servers.set(serverName, connection);
                return { serverName, success: true };
            } catch (error) {
                console.error(`⚠️  Failed to connect to "${serverName}":`, error);
                return { serverName, success: false };
            }
        });

        const results = await Promise.all(connectionPromises);
        const successCount = results.filter(r => r.success).length;

        console.log(`\n✅ Successfully connected to ${successCount}/${serverNames.length} server(s)`);

        // Load tools from all connected servers
        await this.loadAllTools();
    }

    /**
     * Load all tools from all connected servers
     */
    private async loadAllTools(): Promise<void> {
        this.allTools = [];
        this.toolToServerMap.clear();

        console.log(`📦 Loading tools from all servers...`);

        for (const [serverName, connection] of this.servers.entries()) {
            try {
                const tools = await connection.listTools(); for (const tool of tools) {
                    // Store mapping (support both prefixed and unprefixed names)
                    this.toolToServerMap.set(`${serverName}__${tool.name}`, serverName);
                    this.toolToServerMap.set(tool.name, serverName);
                    this.allTools.push(tool);
                    console.log(`   ✓ ${serverName}: ${tool.name}`);
                }
            } catch (error: any) {
                console.log(`❌ Failed to load tools from "${serverName}":`, error.message);
            }
        }

        console.log(`✅ Loaded ${this.allTools.length} tool(s) from ${this.servers.size} server(s)`);
    }

    /**
     * Get all available tools from all servers
     */
    getAllTools(): MCPTool[] {
        return this.allTools;
    }

    /**
     * Call a tool (automatically routes to correct server)
     */
    async callTool(toolName: string, args: Record<string, unknown>): Promise<ToolCallResult> {
        // Try to find the server for this tool
        let serverName = this.toolToServerMap.get(toolName);

        // If prefixed name, extract actual tool name
        let actualToolName = toolName;
        if (toolName.includes('__')) {
            const parts = toolName.split('__');
            serverName = parts[0];
            actualToolName = parts.slice(1).join('__');
        }

        if (!serverName) {
            throw new Error(`Tool "${toolName}" not found in any server`);
        }

        const connection = this.servers.get(serverName);
        if (!connection) {
            throw new Error(`Server "${serverName}" not found`);
        }

        return await connection.callTool(actualToolName, args);
    }    /**
     * Disconnect all servers
     */
    async disconnectAll(): Promise<void> {
        const disconnectPromises = Array.from(this.servers.values()).map(
            connection => connection.disconnect()
        );

        await Promise.all(disconnectPromises);
        this.servers.clear();
        this.toolToServerMap.clear();
        this.allTools = [];
        console.log('\n✅ All connections closed');
    }    /**
     * Display statistics
     */
    displayStats(): void {
        console.log(`\n📊 MCP Manager Statistics:`);
        console.log(`   - Connected Servers: ${this.servers.size}`);
        console.log(`   - Loaded Tools: ${this.allTools.length}`);
        console.log(`   - Server List: ${Array.from(this.servers.keys()).join(', ')}`);
    }
}
