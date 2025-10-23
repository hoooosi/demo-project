import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath } from '../utils/index.js';

export const getFileInfoConfig = {
    title: 'Get File Info',
    description: 'Get detailed information about a file or directory including size, permissions, timestamps',
    inputSchema: {
        path: z.string().describe('Path to the file or directory'),
        includeChecksum: z.boolean().default(false).describe('Include file checksum (MD5)'),
        includePermissions: z.boolean().default(true).describe('Include file permissions'),
        includeLineCount: z.boolean().default(false).describe('Include line count for text files')
    }
};

export async function getFileInfo(params: any) {
    const { path: filePath, includeChecksum, includePermissions, includeLineCount } = params;
    const validatedPath = validatePath(filePath);

    const stats = await fs.stat(validatedPath);
    const parsedPath = path.parse(validatedPath);

    const info: any = {
        path: filePath,
        absolutePath: validatedPath,
        name: parsedPath.name,
        extension: parsedPath.ext,
        type: stats.isDirectory() ? 'directory' : (stats.isSymbolicLink() ? 'symlink' : 'file'),
        size: stats.size,
        sizeFormatted: formatBytes(stats.size),
        created: stats.birthtime,
        modified: stats.mtime,
        accessed: stats.atime
    };

    if (includePermissions) {
        info.permissions = {
            mode: stats.mode,
            readable: !!(stats.mode & fs.constants.R_OK),
            writable: !!(stats.mode & fs.constants.W_OK),
            executable: !!(stats.mode & fs.constants.X_OK)
        };
    }

    if (stats.isFile()) {
        if (includeLineCount) {
            try {
                const content = await fs.readFile(validatedPath, 'utf-8');
                const lines = content.split('\n');
                info.lineCount = lines.length;
                info.isEmpty = content.length === 0;
            } catch (error) {
                // Not a text file or can't be read
                info.lineCount = null;
            }
        }

        if (includeChecksum) {
            try {
                const crypto = await import('crypto');
                const content = await fs.readFile(validatedPath);
                const hash = crypto.createHash('md5');
                hash.update(content);
                info.checksum = {
                    algorithm: 'md5',
                    value: hash.digest('hex')
                };
            } catch (error) {
                info.checksum = null;
            }
        }
    }

    if (stats.isDirectory()) {
        const entries = await fs.readdir(validatedPath);
        info.childrenCount = entries.length;
    }

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(info, null, 2) }],
        structuredContent: info
    };
}

function formatBytes(bytes: number): string {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${(bytes / Math.pow(k, i)).toFixed(2)} ${sizes[i]}`;
}
