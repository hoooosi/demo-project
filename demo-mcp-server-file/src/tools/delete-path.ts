import { z } from 'zod';
import fs from 'fs/promises';
import { validatePath, pathExists, getStats } from '../utils/index.js';

export const deletePathConfig = {
    title: 'Delete Path',
    description: 'Delete a file or directory (optionally recursive for directories)',
    inputSchema: {
        path: z.string().describe('Path to the file or directory to delete'),
        recursive: z.boolean().default(false).describe('If true, recursively delete directories and their contents. Required for non-empty directories'),
        force: z.boolean().default(false).describe('If true, ignore errors if path does not exist')
    }
};

export async function deletePath(params: any) {
    const { path: filePath, recursive, force } = params;
    const validatedPath = validatePath(filePath);

    // Check if path exists
    const exists = await pathExists(validatedPath);
    if (!exists) {
        if (force) {
            return {
                content: [{ type: 'text' as const, text: `Path does not exist (ignored with force=true): ${filePath}` }],
                structuredContent: {
                    path: filePath,
                    deleted: false,
                    reason: 'Path does not exist'
                }
            };
        }
        throw new Error(`Path does not exist: ${filePath}`);
    }

    // Get stats to determine if it's a file or directory
    const stats = await getStats(validatedPath);
    const isDirectory = stats.isDirectory();
    const isFile = stats.isFile();

    // Delete the path
    if (isDirectory) {
        if (recursive) {
            await fs.rm(validatedPath, { recursive: true, force: force });
        } else {
            // Try to delete empty directory
            try {
                await fs.rmdir(validatedPath);
            } catch (error: any) {
                if (error.code === 'ENOTEMPTY') {
                    throw new Error(`Directory is not empty. Use recursive=true to delete non-empty directories: ${filePath}`);
                }
                throw error;
            }
        }
    } else {
        await fs.unlink(validatedPath);
    }

    return {
        content: [{ type: 'text' as const, text: `Successfully deleted ${isDirectory ? 'directory' : 'file'}: ${filePath}` }],
        structuredContent: {
            path: filePath,
            deleted: true,
            type: isDirectory ? 'directory' : 'file',
            wasRecursive: isDirectory && recursive
        }
    };
}
