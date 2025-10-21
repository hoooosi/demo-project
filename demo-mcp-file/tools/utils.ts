import path from 'path';
import fs from 'fs/promises';
import { ALLOWED_DIRS } from '../config.js';

/**
 * Validates if a path is within the allowed directories
 */
export function validatePath(filePath: string): string {
    const absolutePath = path.resolve(filePath);

    if (ALLOWED_DIRS.length === 0) {
        throw new Error('No allowed directories configured');
    }

    const isAllowed = ALLOWED_DIRS.some(dir => {
        const absoluteAllowedDir = path.resolve(dir);
        return absolutePath.startsWith(absoluteAllowedDir);
    });

    if (!isAllowed) {
        throw new Error(`Access denied: path is outside allowed directories`);
    }

    return absolutePath;
}

/**
 * Checks if a path exists
 */
export async function pathExists(filePath: string): Promise<boolean> {
    try {
        await fs.access(filePath);
        return true;
    } catch {
        return false;
    }
}

/**
 * Gets file stats
 */
export async function getStats(filePath: string) {
    return await fs.stat(filePath);
}
