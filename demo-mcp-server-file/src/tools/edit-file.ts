import { z } from 'zod';
import fs from 'fs/promises';
import { validatePath } from '../utils/index.js';

export const editFileConfig = {
    title: 'Edit File',
    description: 'Edit a file with various modes: replace (find and replace), insert (insert at line), delete (delete lines), replace-range (replace line range)',
    inputSchema: {
        path: z.string().describe('Path to the file to edit'),
        mode: z.enum(['replace', 'insert', 'delete', 'replace-range']).describe('Edit mode: replace (find and replace text), insert (insert at line), delete (delete line range), replace-range (replace specific line range)'),
        search: z.string().optional().describe('Text to search for (replace mode)'),
        replacement: z.string().optional().describe('Replacement text (replace mode)'),
        content: z.string().optional().describe('Content to insert (insert mode) or replace with (replace-range mode)'),
        lineNumber: z.number().int().min(1).optional().describe('Line number for insert mode (1-indexed)'),
        startLine: z.number().int().min(1).optional().describe('Start line for delete/replace-range mode (1-indexed)'),
        endLine: z.number().int().min(1).optional().describe('End line for delete/replace-range mode (1-indexed)'),
        replaceAll: z.boolean().default(false).describe('Replace all occurrences in replace mode'),
        backup: z.boolean().default(false).describe('Create backup before editing')
    }
};

export async function editFile(params: any) {
    const { path: filePath, mode, search, replacement, content, lineNumber, startLine, endLine, replaceAll, backup } = params;
    const validatedPath = validatePath(filePath);

    // Create backup if requested
    if (backup) {
        const backupPath = `${validatedPath}.backup`;
        await fs.copyFile(validatedPath, backupPath);
    }

    const fileContent = await fs.readFile(validatedPath, 'utf-8');
    let lines = fileContent.split('\n');
    let result: string;
    let metadata: any = { mode, originalLines: lines.length };

    switch (mode) {
        case 'replace':
            if (!search || replacement === undefined) {
                throw new Error('search and replacement are required for replace mode');
            }
            if (replaceAll) {
                result = fileContent.replace(new RegExp(search.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g'), replacement);
                metadata.replacements = (fileContent.match(new RegExp(search.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g')) || []).length;
            } else {
                result = fileContent.replace(search, replacement);
                metadata.replacements = fileContent.includes(search) ? 1 : 0;
            }
            break;

        case 'insert':
            if (!lineNumber || content === undefined) {
                throw new Error('lineNumber and content are required for insert mode');
            }
            lines.splice(lineNumber - 1, 0, content);
            result = lines.join('\n');
            metadata.insertedAt = lineNumber;
            metadata.newLines = lines.length;
            break;

        case 'delete':
            if (!startLine || !endLine) {
                throw new Error('startLine and endLine are required for delete mode');
            }
            if (startLine > endLine) {
                throw new Error('startLine must be less than or equal to endLine');
            }
            const deletedLines = lines.splice(startLine - 1, endLine - startLine + 1);
            result = lines.join('\n');
            metadata.deletedLines = deletedLines.length;
            metadata.newLines = lines.length;
            break;

        case 'replace-range':
            if (!startLine || !endLine || content === undefined) {
                throw new Error('startLine, endLine, and content are required for replace-range mode');
            }
            if (startLine > endLine) {
                throw new Error('startLine must be less than or equal to endLine');
            }
            lines.splice(startLine - 1, endLine - startLine + 1, content);
            result = lines.join('\n');
            metadata.replacedLines = endLine - startLine + 1;
            metadata.newLines = lines.length;
            break;

        default:
            throw new Error(`Unknown edit mode: ${mode}`);
    }

    await fs.writeFile(validatedPath, result, 'utf-8');

    return {
        content: [{ type: 'text' as const, text: `File edited successfully: ${filePath}` }],
        structuredContent: { path: filePath, ...metadata }
    };
}
