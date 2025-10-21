import { z } from 'zod';
import fs from 'fs/promises';
import path from 'path';
import { validatePath } from './utils.js';

export const searchFilesConfig = {
    title: 'Search Files',
    description: 'Search for files by name or content with advanced filtering options',
    inputSchema: {
        path: z.string().describe('Directory path to search in'),
        query: z.string().describe('Search query (text to find in files)'),
        mode: z.enum(['filename', 'content', 'both']).default('both').describe('Search mode: filename (search in filenames), content (search in file contents), both'),
        caseSensitive: z.boolean().default(false).describe('Case sensitive search'),
        recursive: z.boolean().default(true).describe('Search recursively in subdirectories'),
        maxDepth: z.number().int().min(1).optional().describe('Maximum depth for recursive search'),
        extensions: z.array(z.string()).optional().describe('Filter by file extensions (e.g., [".ts", ".js"])'),
        includeHidden: z.boolean().default(false).describe('Include hidden files'),
        maxResults: z.number().int().min(1).default(100).describe('Maximum number of results to return'),
        contextLines: z.number().int().min(0).default(2).describe('Number of context lines to show around matches in content search')
    }
};

interface SearchResult {
    path: string;
    type: 'filename' | 'content';
    matches?: Array<{
        line: number;
        column: number;
        text: string;
        context: string[];
    }>;
}

async function searchInDirectory(
    dirPath: string,
    query: string,
    options: {
        mode: 'filename' | 'content' | 'both';
        caseSensitive: boolean;
        recursive: boolean;
        maxDepth?: number;
        extensions?: string[];
        includeHidden: boolean;
        maxResults: number;
        contextLines: number;
        currentDepth: number;
        results: SearchResult[];
    }
): Promise<void> {
    const {
        mode, caseSensitive, recursive, maxDepth, extensions,
        includeHidden, maxResults, contextLines, currentDepth, results
    } = options;

    if (results.length >= maxResults) {
        return;
    }

    if (maxDepth && currentDepth > maxDepth) {
        return;
    }

    const entries = await fs.readdir(dirPath, { withFileTypes: true });
    const searchQuery = caseSensitive ? query : query.toLowerCase();

    for (const entry of entries) {
        if (results.length >= maxResults) {
            break;
        }

        if (!includeHidden && entry.name.startsWith('.')) {
            continue;
        }

        const fullPath = path.join(dirPath, entry.name);

        // Search in filename
        if (mode === 'filename' || mode === 'both') {
            const fileName = caseSensitive ? entry.name : entry.name.toLowerCase();
            if (fileName.includes(searchQuery)) {
                results.push({
                    path: fullPath,
                    type: 'filename'
                });
            }
        }

        // Search in file content
        if ((mode === 'content' || mode === 'both') && entry.isFile()) {
            if (extensions) {
                const ext = path.extname(entry.name);
                if (!extensions.includes(ext)) {
                    continue;
                }
            }

            try {
                const content = await fs.readFile(fullPath, 'utf-8');
                const lines = content.split('\n');
                const matches: SearchResult['matches'] = []; for (let i = 0; i < lines.length; i++) {
                    const line = lines[i];
                    if (!line) continue;
                    const searchLine = caseSensitive ? line : line.toLowerCase();

                    if (searchLine.includes(searchQuery)) {
                        const column = searchLine.indexOf(searchQuery);
                        const contextStart = Math.max(0, i - contextLines);
                        const contextEnd = Math.min(lines.length - 1, i + contextLines);
                        const context = lines.slice(contextStart, contextEnd + 1);

                        matches.push({
                            line: i + 1,
                            column: column + 1,
                            text: line,
                            context
                        });
                    }
                }

                if (matches.length > 0) {
                    results.push({
                        path: fullPath,
                        type: 'content',
                        matches
                    });
                }
            } catch (error) {
                // Skip files that can't be read as text
            }
        }

        // Recurse into directories
        if (recursive && entry.isDirectory()) {
            await searchInDirectory(fullPath, query, {
                ...options,
                currentDepth: currentDepth + 1
            });
        }
    }
}

export async function searchFiles(params: any) {
    const {
        path: searchPath, query, mode, caseSensitive, recursive,
        maxDepth, extensions, includeHidden, maxResults, contextLines
    } = params;
    const validatedPath = validatePath(searchPath);

    const results: SearchResult[] = [];

    await searchInDirectory(validatedPath, query, {
        mode,
        caseSensitive,
        recursive,
        maxDepth,
        extensions,
        includeHidden,
        maxResults,
        contextLines,
        currentDepth: 1,
        results
    });

    return {
        content: [{ type: 'text' as const, text: JSON.stringify(results, null, 2) }],
        structuredContent: {
            query,
            resultsCount: results.length,
            truncated: results.length >= maxResults,
            results
        }
    };
}
