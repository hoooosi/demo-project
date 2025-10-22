import { z } from 'zod';
import fs from 'fs/promises';
import { validatePath } from '../utils/index.js';

export const readFileConfig = {
    title: 'Read File',
    description: 'Read a file with various modes: full (entire file), range (specific lines), head (first n lines), tail (last n lines)',
    inputSchema: {
        path: z.string().describe('Path to the file to read'),
        mode: z.enum(['full', 'range', 'head', 'tail']).default('full').describe('Read mode: full (entire file), range (specific lines), head (first n lines), tail (last n lines)'),
        startLine: z.number().int().min(1).optional().describe('Starting line number for range mode (1-indexed)'),
        endLine: z.number().int().min(1).optional().describe('Ending line number for range mode (1-indexed)'),
        lineCount: z.number().int().min(1).optional().describe('Number of lines to read for head/tail mode')
    }
}

export async function readFile(params: any) {
    const { path, mode, startLine, endLine, lineCount } = params;
    const validatedPath = validatePath(path);

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

        case 'range':
            if (!startLine || !endLine) {
                throw new Error('startLine and endLine are required for range mode');
            }
            if (startLine > endLine) {
                throw new Error('startLine must be less than or equal to endLine');
            }
            result = lines.slice(startLine - 1, endLine).join('\n');
            metadata.startLine = startLine;
            metadata.endLine = endLine;
            metadata.linesRead = endLine - startLine + 1;
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

    return {
        content: [{ type: 'text' as const, text: result }],
        structuredContent: { content: result, metadata }
    };
}
