import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath, pathExists } from '../utils/index.js';

export const moveFileConfig = {
    title: 'Move/Copy/Rename File',
    description: 'Move, copy, or rename files and directories',
    inputSchema: {
        sourcePath: z.string().describe('Source path of the file or directory to move'),
        destinationPath: z.string().describe('Destination path'),
        overwrite: z.boolean().default(false).describe('Overwrite destination if it exists'),
        createDirectories: z.boolean().default(true).describe('Create parent directories if they don\'t exist'),
        mode: z.enum(['move', 'copy', 'rename']).default('move').describe('Operation mode: move (cut and paste), copy (duplicate), rename (rename in same directory)')
    }
};

export async function moveFile(params: any) {
    const { sourcePath, destinationPath, overwrite, createDirectories, mode } = params;
    const validatedSource = validatePath(sourcePath);
    const validatedDest = validatePath(destinationPath);

    const sourceExists = await pathExists(validatedSource);
    if (!sourceExists) {
        throw new Error(`Source does not exist: ${sourcePath}`);
    }

    const destExists = await pathExists(validatedDest);
    if (destExists && !overwrite) {
        throw new Error(`Destination already exists: ${destinationPath}`);
    }

    // Create parent directories if needed
    if (createDirectories) {
        const destDir = path.dirname(validatedDest);
        await fs.mkdir(destDir, { recursive: true });
    }

    const sourceStats = await fs.stat(validatedSource);

    switch (mode) {
        case 'copy':
            if (sourceStats.isDirectory()) {
                await fs.cp(validatedSource, validatedDest, { recursive: true, force: overwrite });
            } else {
                await fs.copyFile(validatedSource, validatedDest);
            }
            break;

        case 'move':
        case 'rename':
            await fs.rename(validatedSource, validatedDest);
            break;

        default:
            throw new Error(`Unknown mode: ${mode}`);
    }

    return {
        content: [{ type: 'text' as const, text: `Successfully ${mode}d: ${sourcePath} -> ${destinationPath}` }],
        structuredContent: {
            operation: mode,
            sourcePath,
            destinationPath,
            overwritten: destExists,
            type: sourceStats.isDirectory() ? 'directory' : 'file'
        }
    };
}
