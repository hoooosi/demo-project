/**
 * Global configuration for the MCP file operations server
 */

// Configure allowed directories - IMPORTANT: Set these to your desired paths
// Can be configured via environment variable: ALLOWED_DIRS="./data,./workspace,/path/to/folder"
export const ALLOWED_DIRS = process.env.ALLOWED_DIRS
    ? process.env.ALLOWED_DIRS.split(',').map(dir => dir.trim())
    : ['C:\\Users\\Administrator\\Desktop\\test'];

// Port configuration
export const PORT = parseInt(process.env.PORT || '3000');
