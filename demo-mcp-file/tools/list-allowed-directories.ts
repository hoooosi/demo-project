import { z } from 'zod';
import path from 'path';
import { ALLOWED_DIRS } from '../config.js';

export const listAllowedDirectoriesConfig = {
    title: 'List Allowed Directories',
    description: 'List all directories that file operations are restricted to',
    inputSchema: {
        absolute: z.boolean().default(true).describe('Return absolute paths')
    }
};

export async function listAllowedDirectories(params: any) {
    const { absolute } = params;

    const directories = absolute
        ? ALLOWED_DIRS.map(dir => path.resolve(dir))
        : ALLOWED_DIRS;

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(directories, null, 2) }],
        structuredContent: {
            directories,
            count: directories.length
        }
    };
}
