/**
 * MCP Multi-Server Client - Entry Point
 * 
 * This client loads ALL MCP servers from mcp-config.json
 * and aggregates their tools for the AI agent to use.
 */

import {
    MCPManager,
    Agent,
    loadMCPConfig,
    displayWelcome,
    startInteractiveChatLoop
} from './src/index';

/**
 * Main entry point
 */
async function main() {
    try {
        displayWelcome();

        // Create MCP Manager and initialize all servers
        const mcpManager = new MCPManager(await loadMCPConfig());
        await mcpManager.init();

        // Display statistics
        mcpManager.displayStats();

        // Create Agent with all MCP tools
        const agent = new Agent(mcpManager, {
            model: 'gemini-2.5-flash-preview-05-20',
        });

        // Start interactive chat
        await startInteractiveChatLoop(async (message) => {
            await agent.chat(message);
        });

        // Cleanup
        await mcpManager.disconnectAll();
    } catch (error) {
        console.error('\n❌ Fatal Error:', error);
        process.exit(1);
    }
}

// Run the application
main().catch(console.error);
