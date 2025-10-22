import { z } from 'zod';
import path from 'path';
import { WORKSPACE_PATH } from '../../config.js';

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
        ? WORKSPACE_PATH.map(dir => path.resolve(dir))
        : WORKSPACE_PATH;

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(directories, null, 2) }],
        structuredContent: {
            directories,
            count: directories.length
        }
    };
}
