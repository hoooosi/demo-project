/**
 * MCP Multi-Server Client - Main Module Exports
 */

export { MCPManager } from './mcp/mcp-manager';
export { MCPServerConnection } from './mcp/mcp-server-connection';
export { Agent } from './agent';
export { loadMCPConfig, getDefaultModel } from './utils/config-loader';
export { createOpenAIClient, mcpToolToOpenAITool, extractTextFromToolResult } from './utils/openai-integration';
export { getUserInput, startInteractiveChatLoop, startInteractiveChatLoopStream, displayWelcome } from './utils/cli';
export type { MCPConfig, MCPServerConfig, MCPTool, ToolCallResult, ServerInfo } from './types';
