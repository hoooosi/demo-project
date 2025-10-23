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
    startInteractiveChatLoopStream,
    startInteractiveChatLoop,
    getDefaultModel
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

        // Create Agent with all MCP tools (using env variables)
        const agent = new Agent(mcpManager, {
            model: getDefaultModel(),
        });

        console.log(`\n📝 Using model: ${getDefaultModel()}`);

        // Start interactive chat
        await startInteractiveChatLoopStream((message) => {
            return agent.chatStream(message);
        });


        // await startInteractiveChatLoop(async (message) => {
        //     await agent.chat(message);
        // });

        // Cleanup
        await mcpManager.disconnectAll();
    } catch (error) {
        console.error('\n❌ Fatal Error:', error);
        process.exit(1);
    }
}

// Run the application
main().catch(console.error);
