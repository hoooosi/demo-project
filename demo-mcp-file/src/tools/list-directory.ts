import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath } from '../utils/index.js';

export const listDirectoryConfig = {
    title: 'List Directory',
    description: 'List directory contents with filtering and recursive options',
    inputSchema: {
        path: z.string().describe('Path to the directory to list'),
        recursive: z.boolean().default(false).describe('List subdirectories recursively'),
        includeHidden: z.boolean().default(false).describe('Include hidden files and directories'),
        fileTypes: z.array(z.enum(['file', 'directory', 'symlink'])).optional().describe('Filter by file types'),
        extensions: z.array(z.string()).optional().describe('Filter by file extensions (e.g., [".ts", ".js"])'),
        maxDepth: z.number().int().min(1).optional().describe('Maximum depth for recursive listing')
    }
};

async function listDir(
    dirPath: string,
    options: {
        recursive: boolean;
        includeHidden: boolean;
        fileTypes?: ('file' | 'directory' | 'symlink')[];
        extensions?: string[];
        maxDepth?: number;
        currentDepth?: number;
    }
): Promise<any[]> {
    const { recursive, includeHidden, fileTypes, extensions, maxDepth, currentDepth = 1 } = options;

    const entries = await fs.readdir(dirPath, { withFileTypes: true });
    const results: any[] = [];

    for (const entry of entries) {
        // Skip hidden files if not included
        if (!includeHidden && entry.name.startsWith('.')) {
            continue;
        }

        const fullPath = path.join(dirPath, entry.name);
        const stats = await fs.stat(fullPath);

        let type: 'file' | 'directory' | 'symlink';
        if (entry.isSymbolicLink()) {
            type = 'symlink';
        } else if (entry.isDirectory()) {
            type = 'directory';
        } else {
            type = 'file';
        }

        // Filter by file types
        if (fileTypes && !fileTypes.includes(type)) {
            continue;
        }

        // Filter by extensions
        if (extensions && type === 'file') {
            const ext = path.extname(entry.name);
            if (!extensions.includes(ext)) {
                continue;
            }
        } const item: any = {
            name: entry.name,
            path: fullPath,
            type,
            size: stats.size,
            modified: stats.mtime,
            created: stats.birthtime
        };

        if (recursive && entry.isDirectory() && (!maxDepth || currentDepth < maxDepth)) {
            item['children'] = await listDir(fullPath, {
                ...options,
                currentDepth: currentDepth + 1
            });
        }

        results.push(item);
    }

    return results;
}

export async function listDirectory(params: any) {
    const { path: dirPath, recursive, includeHidden, fileTypes, extensions, maxDepth } = params;
    const validatedPath = validatePath(dirPath);

    const results = await listDir(validatedPath, {
        recursive,
        includeHidden,
        fileTypes,
        extensions,
        maxDepth
    });

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(results, null, 2) }],
        structuredContent: {
            path: dirPath,
            count: results.length,
            items: results
        }
    };
}
