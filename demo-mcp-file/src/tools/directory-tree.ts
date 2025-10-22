import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath } from '../utils/index.js';

export const directoryTreeConfig = {
    title: 'Directory Tree',
    description: 'Generate a visual tree representation of a directory structure',
    inputSchema: {
        path: z.string().describe('Path to the directory to generate tree for'),
        maxDepth: z.number().int().min(1).default(3).describe('Maximum depth to traverse'),
        includeHidden: z.boolean().default(false).describe('Include hidden files and directories'),
        includeSize: z.boolean().default(true).describe('Include file sizes in output'),
        format: z.enum(['ascii', 'json']).default('ascii').describe('Output format')
    }
};

interface TreeNode {
    name: string;
    type: 'file' | 'directory';
    size?: number;
    children?: TreeNode[];
}

async function buildTree(
    dirPath: string,
    options: {
        maxDepth: number;
        includeHidden: boolean;
        includeSize: boolean;
        currentDepth: number;
    }
): Promise<TreeNode[]> {
    const { maxDepth, includeHidden, includeSize, currentDepth } = options;

    if (currentDepth > maxDepth) {
        return [];
    }

    const entries = await fs.readdir(dirPath, { withFileTypes: true });
    const results: TreeNode[] = [];

    for (const entry of entries) {
        if (!includeHidden && entry.name.startsWith('.')) {
            continue;
        }

        const fullPath = path.join(dirPath, entry.name);
        const stats = await fs.stat(fullPath);

        const node: TreeNode = {
            name: entry.name,
            type: entry.isDirectory() ? 'directory' : 'file'
        };

        if (includeSize) {
            node.size = stats.size;
        }

        if (entry.isDirectory()) {
            node.children = await buildTree(fullPath, {
                ...options,
                currentDepth: currentDepth + 1
            });
        }

        results.push(node);
    }

    return results.sort((a, b) => {
        // Directories first, then alphabetically
        if (a.type !== b.type) {
            return a.type === 'directory' ? -1 : 1;
        }
        return a.name.localeCompare(b.name);
    });
}

function formatTreeAscii(nodes: TreeNode[], prefix: string = '', includeSize: boolean = true): string {
    let output = '';

    nodes.forEach((node, index) => {
        const isLast = index === nodes.length - 1;
        const connector = isLast ? '└── ' : '├── ';
        const sizeStr = includeSize && node.type === 'file' ? ` (${formatBytes(node.size || 0)})` : '';

        output += `${prefix}${connector}${node.name}${sizeStr}\n`;

        if (node.children && node.children.length > 0) {
            const newPrefix = prefix + (isLast ? '    ' : '│   ');
            output += formatTreeAscii(node.children, newPrefix, includeSize);
        }
    });

    return output;
}

function formatBytes(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${(bytes / Math.pow(k, i)).toFixed(1)} ${sizes[i]}`;
}

export async function directoryTree(params: any) {
    const { path: dirPath, maxDepth, includeHidden, includeSize, format } = params;
    const validatedPath = validatePath(dirPath);

    const tree = await buildTree(validatedPath, {
        maxDepth,
        includeHidden,
        includeSize,
        currentDepth: 1
    });

    let output: string;
    if (format === 'ascii') {
        output = `${dirPath}\n${formatTreeAscii(tree, '', includeSize)}`;
    } else {
        output = JSON.stringify(tree, null, 2);
    }

    return {
        content: [{ type: 'text' as const, text: output }],
        structuredContent: {
            path: dirPath,
            format,
            tree
        }
    };
}
