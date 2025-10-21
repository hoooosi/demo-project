import { z } from 'zod';
import fs from 'fs/promises';
import { validatePath } from './utils.js';

export const readMultipleFilesConfig = {
    title: 'Read Multiple Files',
    description: 'Read multiple files at once with support for different read modes',
    inputSchema: {
        paths: z.array(z.string()).describe('Array of file paths to read'),
        mode: z.enum(['full', 'head', 'tail']).default('full').describe('Read mode for all files'),
        lineCount: z.number().int().min(1).optional().describe('Number of lines to read for head/tail mode'),
        continueOnError: z.boolean().default(false).describe('Continue reading other files if one fails')
    }
};

export async function readMultipleFiles(params: any) {
    const { paths, mode, lineCount, continueOnError } = params;

    const results: Array<{
        path: string;
        success: boolean;
        content?: string;
        error?: string;
        metadata?: any;
    }> = []; for (const filePath of paths) {
        try {
            const validatedPath = validatePath(filePath);
            const content = await fs.readFile(validatedPath, 'utf-8');
            const lines = content.split('\n');

            let result: string;
            let metadata: any = {
                totalLines: lines.length,
                mode
            };

            switch (mode) {
                case 'full':
                    result = content;
                    break;
                case 'head':
                    if (!lineCount) {
                        throw new Error('lineCount is required for head mode');
                    }
                    result = lines.slice(0, lineCount).join('\n');
                    metadata.lineCount = lineCount;
                    metadata.linesRead = Math.min(lineCount, lines.length);
                    break;
                case 'tail':
                    if (!lineCount) {
                        throw new Error('lineCount is required for tail mode');
                    }
                    result = lines.slice(-lineCount).join('\n');
                    metadata.lineCount = lineCount;
                    metadata.linesRead = Math.min(lineCount, lines.length);
                    break;
                default:
                    result = content;
            }

            results.push({
                path: filePath,
                success: true,
                content: result,
                metadata
            });
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'Unknown error';
            if (!continueOnError) {
                throw error;
            }
            results.push({
                path: filePath,
                success: false,
                error: errorMessage
            });
        }
    }

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(results, null, 2) }],
        structuredContent: { files: results, totalFiles: paths.length }
    };
}
