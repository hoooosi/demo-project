# 文件操作 MCP 服务

这是练习项目，一个基于 Model Context Protocol (MCP) 的文件操作服务，使用 Node.js 和 TypeScript 构建。

## Task

- [x] 调整工作目录为单个，允许相对目录
- [x] 添加删除文件/文件夹功能
- [ ] 优化提示词
- [ ] 添加文件监听功能
- [ ] 支持配置调用方式(stdio 等等)

## 功能特性

该服务提供了以下文件操作工具：

### 工具 (Tools)

1. **read_file** - 读取文件内容（支持多种读取模式）
2. **read_multiple_files** - 批量读取多个文件
3. **write_file** - 写入文件内容
4. **edit_file** - 编辑现有文件
5. **create_directory** - 创建目录
6. **list_directory** - 列出目录内容
7. **directory_tree** - 显示目录树结构
8. **move_file** - 移动/重命名文件
9. **search_files** - 搜索文件
10. **get_file_info** - 获取文件信息
11. **delete_path** - 删除文件或目录

### 安全特性

- 🔒 所有路径自动规范化和验证
- 🔒 禁止访问工作目录之外的任何文件
- 🔒 防止路径遍历攻击（如 `../../../`）
- 🔒 相对路径自动解析为工作目录内的绝对路径

## 安装

```bash
bun install
# 或使用 npm
npm install
```

## 配置

### 1. 环境变量配置

在运行服务之前，你可以通过环境变量配置工作目录和端口：

```bash
# Windows PowerShell
$env:ALLOWED_DIRS="/workspace"
$env:PORT="3000"

# Linux/macOS
export ALLOWED_DIRS="/workspace"
export PORT=3000
```

### 2. 直接修改配置文件

编辑 `config.ts` 文件，修改以下配置：

```typescript
export const WORKSPACE_PATH = "/workspace";
export const PORT = 3000;
```

## 运行

```bash
bun run server.ts
# 或使用 npm
npm start
```

服务启动后，将在 `http://localhost:3000/mcp` 上运行。

## MCP 客户端配置

```json
{
  "mcpServers": {
    "file-operations": {
      "command": "node",
      "args": ["PATH/server.ts"],
      "env": {
        "ALLOWED_DIRS": "/workspace",
        "PORT": "3000"
      }
    }
  }
}
```
