import OpenAI from 'openai';
import type { ChatCompletionTool } from 'openai/resources/chat/completions';
import type { MCPTool } from '../types';
import 'dotenv/config';

/**
 * Convert MCP tool schema to OpenAI function schema
 */
export function mcpToolToOpenAITool(mcpTool: MCPTool): ChatCompletionTool {
    return {
        type: 'function',
        function: {
            name: mcpTool.name,
            description: mcpTool.description || '',
            parameters: mcpTool.inputSchema || { type: 'object', properties: {} },
        },
    };
}

/**
 * Extract text content from tool call result
 */
export function extractTextFromToolResult(toolResult: any): string {
    let resultContent = '';

    if (toolResult.content && Array.isArray(toolResult.content)) {
        resultContent = toolResult.content
            .map((item: any) => {
                if (item.type === 'text') {
                    return item.text;
                } else if (item.type === 'image') {
                    return `[Image: ${item.data?.substring(0, 50)}...]`;
                } else if (item.type === 'resource') {
                    return `[Resource: ${JSON.stringify(item.resource)}]`;
                }
                return '';
            })
            .join('\n');
    }

    return resultContent || 'Tool executed successfully';
}

/**
 * Create OpenAI client with custom configuration
 */
export function createOpenAIClient(config?: {
    baseURL?: string;
    apiKey?: string;
}): OpenAI {
    return new OpenAI({
        baseURL: config?.baseURL || process.env.OPENAI_BASE_URL || 'http://192.168.31.3:16000/proxy/gemini/v1beta/openai/',
        apiKey: config?.apiKey || process.env.OPENAI_API_KEY || 'token',
    });
}

/**
 * Get default model from environment or fallback
 */
export function getDefaultModel(): string {
    return process.env.OPENAI_MODEL || 'gemini-2.5-flash-preview-05-20';
}
