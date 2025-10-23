import type { ChatCompletionMessageParam } from 'openai/resources/chat/completions';
import { MCPManager } from './mcp/mcp-manager';
import { createOpenAIClient, mcpToolToOpenAITool, extractTextFromToolResult } from './utils/openai-integration';

/**
 * AI Agent that uses MCP tools
 * Orchestrates conversation between OpenAI and MCP servers
 */
export class Agent {
    private mcpManager: MCPManager;
    private openai: ReturnType<typeof createOpenAIClient>;
    private conversationHistory: ChatCompletionMessageParam[] = [];
    private model: string; constructor(
        mcpManager: MCPManager,
        options?: {
            model?: string;
            openaiConfig?: {
                baseURL?: string;
                apiKey?: string;
            };
        }
    ) {
        this.mcpManager = mcpManager;
        this.openai = createOpenAIClient(options?.openaiConfig);
        this.model = options?.model || 'gemini-2.5-flash-preview-05-20';
    }    /**
     * Execute tool calls and add results to conversation history
     */
    private async executeToolCalls(
        toolCalls: any[],
        yieldOutput?: (message: string) => Promise<void>
    ): Promise<void> {
        for (const toolCall of toolCalls) {
            if (toolCall.type !== 'function') continue;

            try {
                const toolName = toolCall.function.name;
                const toolArgs = JSON.parse(toolCall.function.arguments);

                if (yieldOutput) {
                    await yieldOutput(`   ⏳ Calling tool: ${toolName}...\n`);
                } else {
                    console.log(`   ⏳ Calling tool: ${toolName}...`);
                }

                // Call the tool through MCP Manager (auto-routes to correct server)
                const toolResult = await this.mcpManager.callTool(toolName, toolArgs);

                // Extract text content
                const resultContent = extractTextFromToolResult(toolResult);
                const resultPreview = `${resultContent.substring(0, 200)}${resultContent.length > 200 ? '...' : ''}`;

                if (yieldOutput) {
                    await yieldOutput(`   ✓ Tool result: ${resultPreview}\n`);
                } else {
                    console.log(`   ✓ Tool result: ${resultPreview}`);
                }

                // Add tool result to conversation
                this.conversationHistory.push({
                    role: 'tool',
                    tool_call_id: toolCall.id,
                    content: resultContent,
                });
            } catch (error: any) {
                const errorMsg = `   ✗ Tool error: ${error.message}`;

                if (yieldOutput) {
                    await yieldOutput(errorMsg + '\n');
                } else {
                    console.log(errorMsg);
                }

                this.conversationHistory.push({
                    role: 'tool',
                    tool_call_id: toolCall.id,
                    content: `Error: ${error instanceof Error ? error.message : String(error)}`,
                });
            }
        }

        if (!yieldOutput) {
            console.log("=".repeat(100));
        }
    }

    /**
     * Send a message and get response
     */
    async chat(userMessage: string): Promise<string> {
        // Add user message to history
        this.conversationHistory.push({
            role: 'user',
            content: userMessage,
        });

        // Get all available tools from all MCP servers
        const mcpTools = this.mcpManager.getAllTools();
        const openaiTools = mcpTools.map(mcpToolToOpenAITool);

        let finalResponse = '';
        let continueLoop = true; while (continueLoop) {
            // Call OpenAI with all available tools
            const response = await this.openai.chat.completions.create({
                model: this.model,
                messages: this.conversationHistory,
                tools: openaiTools.length > 0 ? openaiTools : undefined,
                tool_choice: openaiTools.length > 0 ? 'auto' : undefined,
            });

            const message = response.choices[0]?.message;
            if (!message) continue;

            if (message.content) {
                console.log(`🤖 Assistant: ${message.content}`);
                finalResponse = message.content;
            }

            // Add assistant message to history
            this.conversationHistory.push(message);
            if (message.tool_calls && message.tool_calls.length > 0) {
                console.log(`🛠️  Assistant wants to call ${message.tool_calls.length} tool(s)`);
                await this.executeToolCalls(message.tool_calls);
                continue;
            }

            // No more tool calls, exit loop
            continueLoop = false;
        } return finalResponse;
    }

    /**
     * Send a message and get streaming response
     */
    async *chatStream(userMessage: string): AsyncGenerator<string> {
        // Add user message to history
        this.conversationHistory.push({
            role: 'user',
            content: userMessage,
        });

        // Get all available tools from all MCP servers
        const mcpTools = this.mcpManager.getAllTools();
        const openaiTools = mcpTools.map(mcpToolToOpenAITool);

        let continueLoop = true;

        while (continueLoop) {
            // Call OpenAI with streaming enabled
            const stream = await this.openai.chat.completions.create({
                model: this.model,
                messages: this.conversationHistory,
                tools: openaiTools.length > 0 ? openaiTools : undefined,
                tool_choice: openaiTools.length > 0 ? 'auto' : undefined, stream: true,
            });

            let currentContent = '';
            let tool_calls: any[] | undefined = undefined;
            for await (const chunk of stream) {
                const delta = chunk.choices[0]?.delta;
                if (!delta) continue;

                // Handle content streaming
                if (delta.content) {
                    currentContent += delta.content;
                    yield delta.content;
                }

                // Collect tool calls (they come complete in the last chunk for this API)
                if (delta.tool_calls && delta.tool_calls.length > 0) {
                    tool_calls = delta.tool_calls;
                }
            }

            // Add assistant message to history
            this.conversationHistory.push({
                role: 'assistant',
                content: currentContent || null,
                tool_calls
            });

            if (tool_calls && tool_calls.length > 0) {
                await this.executeToolCalls(tool_calls);
                continue;
            }

            // No more tool calls, exit loop
            continueLoop = false;
        }
    }

    /**
     * Get conversation history
     */
    getHistory(): ChatCompletionMessageParam[] {
        return [...this.conversationHistory];
    }

    /**
     * Clear conversation history
     */
    clearHistory(): void {
        this.conversationHistory = [];
    }

    /**
     * Get available tools count
     */
    getToolsCount(): number {
        return this.mcpManager.getAllTools().length;
    }
}
