import { readFile } from 'fs/promises';
import { resolve } from 'path';
import type { MCPConfig, MCPServerConfig } from '../types';

/**
 * Load MCP configuration from a JSON file
 */
export async function loadMCPConfig(configPath: string = './config.json'): Promise<Record<string, MCPServerConfig>> {
    try {
        console.log(`\n📄 Loading MCP configuration from ${configPath}...`);
        const absolutePath = resolve(configPath);
        const fileContent = await readFile(absolutePath, 'utf-8');
        const config = JSON.parse(fileContent) as MCPConfig;

        // Validate configuration
        if (!config.servers || typeof config.servers !== 'object') {
            throw new Error('Invalid configuration: "servers" field is required and must be an object');
        }


        for (const [serverName, serverConfig] of Object.entries(config.servers)) {
            if (!serverConfig.command) {
                throw new Error(`Invalid configuration for server "${serverName}": "command" is required`);
            }
            if (!serverConfig.args || !Array.isArray(serverConfig.args)) {
                throw new Error(`Invalid configuration for server "${serverName}": "args" must be an array`);
            }
            if (serverConfig.type !== 'stdio') {
                throw new Error(`Invalid configuration for server "${serverName}": only "stdio" type is supported`);
            }
        }

        return config.servers;
    } catch (error) {
        if ((error as NodeJS.ErrnoException).code === 'ENOENT') {
            throw new Error(`Configuration file not found: ${configPath}`);
        }
        throw error;
    }
}

/**
 * Get a specific server configuration
 */
export function getServerConfig(config: MCPConfig, serverName: string): MCPServerConfig {
    const serverConfig = config.servers[serverName];
    if (!serverConfig) {
        throw new Error(`Server "${serverName}" not found in configuration`);
    }
    return serverConfig;
}

/**
 * List all available server names
 */
export function listServerNames(config: MCPConfig): string[] {
    return Object.keys(config.servers);
}

/**
 * Get all server configurations
 */
export function getAllServerConfigs(config: MCPConfig): Record<string, MCPServerConfig> {
    return config.servers;
}
