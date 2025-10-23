import path from 'path';
import fs from 'fs/promises';
import { WORKSPACE_PATH } from '../../config.js';

/**
 * Validates if a path is within the allowed directories
 * Supports relative paths - they will be resolved relative to WORKSPACE_PATH
 */
export function validatePath(filePath: string): string {
    if (!WORKSPACE_PATH || WORKSPACE_PATH.length === 0) {
        throw new Error('No allowed directory configured');
    }

    const workspaceAbsolutePath = path.resolve(WORKSPACE_PATH);

    // If the path is relative, resolve it relative to the workspace path
    const absolutePath = path.isAbsolute(filePath)
        ? path.resolve(filePath)
        : path.resolve(workspaceAbsolutePath, filePath);

    // Check if the resolved path is within the workspace
    if (!absolutePath.startsWith(workspaceAbsolutePath)) {
        throw new Error(`Access denied: path is outside allowed directory`);
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
