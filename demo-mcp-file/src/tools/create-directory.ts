import { z } from 'zod';
import fs from 'fs/promises';
import { validatePath, pathExists } from '../utils/index.js';

export const createDirectoryConfig = {
    title: 'Create Directory',
    description: 'Create a new directory with optional recursive creation',
    inputSchema: {
        path: z.string().describe('Path to the directory to create'),
        recursive: z.boolean().default(true).describe('Create parent directories if they don\'t exist'),
        errorIfExists: z.boolean().default(false).describe('Throw error if directory already exists')
    }
};

export async function createDirectory(params: any) {
    const { path: dirPath, recursive, errorIfExists } = params;
    const validatedPath = validatePath(dirPath);

    const exists = await pathExists(validatedPath);

    if (exists && errorIfExists) {
        throw new Error(`Directory already exists: ${dirPath}`);
    }

    if (!exists) {
        await fs.mkdir(validatedPath, { recursive });
    }

    return {
        content: [{ type: 'text' as const, text: `Directory ${exists ? 'already exists' : 'created'}: ${dirPath}` }],
        structuredContent: {
            path: dirPath,
            created: !exists,
            existed: exists
        }
    };
}
