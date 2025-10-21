import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StreamableHTTPServerTransport } from '@modelcontextprotocol/sdk/server/streamableHttp.js';
import express from 'express';
import { ALLOWED_DIRS, PORT } from './config.js';

// Import all tools and configs
import { readFile, readFileConfig } from './tools/read-file.js';
import { readMultipleFiles, readMultipleFilesConfig } from './tools/read-multiple-files.js';
import { writeFile, writeFileConfig } from './tools/write-file.js';
import { editFile, editFileConfig } from './tools/edit-file.js';
import { createDirectory, createDirectoryConfig } from './tools/create-directory.js';
import { listDirectory, listDirectoryConfig } from './tools/list-directory.js';
import { directoryTree, directoryTreeConfig } from './tools/directory-tree.js';
import { moveFile, moveFileConfig } from './tools/move-file.js';
import { searchFiles, searchFilesConfig } from './tools/search-files.js';
import { getFileInfo, getFileInfoConfig } from './tools/get-file-info.js';
import { listAllowedDirectories, listAllowedDirectoriesConfig } from './tools/list-allowed-directories.js';

// Create an MCP server for file operations
const server = new McpServer({
    name: 'file-operations-server',
    version: '1.0.0'
});

// Register read_file tool
server.registerTool('read_file', readFileConfig, (params) => readFile(params));

// Register read_multiple_files tool
server.registerTool('read_multiple_files', readMultipleFilesConfig, (params) => readMultipleFiles(params));

// Register write_file tool
server.registerTool('write_file', writeFileConfig, (params) => writeFile(params));

// Register edit_file tool
server.registerTool('edit_file', editFileConfig, (params) => editFile(params));

// Register create_directory tool
server.registerTool('create_directory', createDirectoryConfig, (params) => createDirectory(params));

// Register list_directory tool
server.registerTool('list_directory', listDirectoryConfig, (params) => listDirectory(params));

// Register directory_tree tool
server.registerTool('directory_tree', directoryTreeConfig, (params) => directoryTree(params));

// Register move_file tool
server.registerTool('move_file', moveFileConfig, (params) => moveFile(params));

// Register search_files tool
server.registerTool('search_files', searchFilesConfig, (params) => searchFiles(params));

// Register get_file_info tool
server.registerTool('get_file_info', getFileInfoConfig, (params) => getFileInfo(params));

// Register list_allowed_directories tool
server.registerTool('list_allowed_directories', listAllowedDirectoriesConfig, (params) => listAllowedDirectories(params));

// Set up Express and HTTP transport
const app = express();
app.use(express.json());

app.post('/mcp', async (req, res) => {
    // Create a new transport for each request to prevent request ID collisions
    const transport = new StreamableHTTPServerTransport({
        sessionIdGenerator: undefined,
        enableJsonResponse: true
    });

    res.on('close', () => {
        transport.close();
    });

    await server.connect(transport);
    await transport.handleRequest(req, res, req.body);
});

const port = PORT;
app.listen(port, () => {
    console.log(`File Operations MCP Server running on http://localhost:${port}/mcp`);
    console.log(`Allowed directories: ${ALLOWED_DIRS.join(', ')}`);
}).on('error', error => {
    console.error('Server error:', error);
    process.exit(1);
});
