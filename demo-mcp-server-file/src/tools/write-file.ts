import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath, pathExists } from '../utils/index.js';

export const writeFileConfig = {
    title: 'Write File',
    description: 'Write to a file with modes: overwrite (replace), append (add to end), prepend (add to beginning)',
    inputSchema: {
        path: z.string().describe('Path to the file to write'),
        content: z.string().describe('Content to write to the file'),
        mode: z.enum(['overwrite', 'append', 'prepend']).default('overwrite').describe('Write mode: overwrite (replace entire file), append (add to end), prepend (add to beginning)'),
        createDirectories: z.boolean().default(true).describe('Create parent directories if they don\'t exist'),
        backup: z.boolean().default(false).describe('Create a backup of existing file before writing')
    }
};

export async function writeFile(params: any) {
    const { path: filePath, content, mode, createDirectories, backup } = params;
    const validatedPath = validatePath(filePath);

    // Create parent directories if needed
    if (createDirectories) {
        const dir = path.dirname(validatedPath);
        await fs.mkdir(dir, { recursive: true });
    }

    const exists = await pathExists(validatedPath);
    let finalContent = content;

    if (exists) {
        // Create backup if requested
        if (backup) {
            const backupPath = `${validatedPath}.backup`;
            await fs.copyFile(validatedPath, backupPath);
        }

        // Handle different write modes
        if (mode === 'append') {
            const existingContent = await fs.readFile(validatedPath, 'utf-8');
            finalContent = existingContent + content;
        } else if (mode === 'prepend') {
            const existingContent = await fs.readFile(validatedPath, 'utf-8');
            finalContent = content + existingContent;
        }
    }

    await fs.writeFile(validatedPath, finalContent, 'utf-8');

    const stats = await fs.stat(validatedPath);

    return {
        content: [{ type: 'text' as const, text: `File written successfully: ${filePath}` }],
        structuredContent: {
            path: filePath,
            mode,
            bytesWritten: stats.size,
            created: !exists,
            backupCreated: backup && exists
        }
    };
}
