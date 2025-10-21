# 文件操作 MCP 服务

这是一个基于 Model Context Protocol (MCP) 的文件操作服务，使用 Node.js 和 TypeScript 构建。

## 任务安排

- [] 调整工作目录为单个，允许相对目录
- [] 添加删除文件/文件夹功能
- [] 优化提示词

## 功能特性

该服务提供了以下文件操作工具：

### 工具 (Tools)

1. **read_file** - 读取文件内容（支持多种读取模式）

   - 参数:
     - `filePath` (字符串，必需) - 要读取的文件路径
     - `mode` (枚举，可选) - 读取模式：
       - `full` - 读取完整文件（默认）
       - `head` - 读取文件前 N 行
       - `tail` - 读取文件后 N 行
       - `range` - 读取指定行范围
     - `lines` (数字，可选) - 要读取的行数（用于 head/tail 模式，默认 100 行）
     - `startLine` (数字，可选) - 起始行号（用于 range 模式，从 1 开始）
     - `endLine` (数字，可选) - 结束行号（用于 range 模式，从 1 开始）
   - 返回: 文件内容、总行数、已读行数、是否截断等信息

2. **write_file** - 写入文件（支持多种写入模式）

   - 参数:
     - `filePath` (字符串，必需) - 文件路径
     - `content` (字符串，必需) - 要写入的内容
     - `mode` (枚举，可选) - 写入模式：
       - `overwrite` - 覆盖整个文件（默认）
       - `append` - 追加到文件末尾
       - `insert` - 在指定行插入内容
       - `replace` - 替换指定行范围
     - `lineNumber` (数字，可选) - 插入位置的行号（用于 insert 模式，从 1 开始）
     - `startLine` (数字，可选) - 起始行号（用于 replace 模式，从 1 开始）
     - `endLine` (数字，可选) - 结束行号（用于 replace 模式，从 1 开始）
   - 返回: 操作结果、写入行数等信息
   - 自动创建父目录

3. **delete_file** - 删除文件

   - 参数: `filePath` (字符串) - 要删除的文件路径

4. **list_directory** - 列出目录内容

   - 参数: `dirPath` (字符串) - 目录路径
   - 返回: 文件和目录列表

5. **create_directory** - 创建目录

   - 参数: `dirPath` (字符串) - 目录路径
   - 自动创建父目录

6. **file_exists** - 检查文件是否存在

   - 参数: `filePath` (字符串) - 文件路径
   - 返回: 是否存在

7. **file_info** - 获取文件信息

   - 参数: `filePath` (字符串) - 文件路径
   - 返回: 文件大小、类型、修改时间等信息

8. **delete_lines** - 删除文件中的指定行范围

   - 参数:
     - `filePath` (字符串，必需) - 文件路径
     - `startLine` (数字，必需) - 起始行号（从 1 开始）
     - `endLine` (数字，必需) - 结束行号（从 1 开始）
   - 返回: 操作结果、删除的行数等信息

9. **get_work_directory** - 获取当前工作目录信息
   - 参数: 无
   - 返回: 工作目录路径、是否存在、描述信息
   - 用于让 AI 了解当前的工作目录限制

### 资源 (Resources)

- **file://{filePath}** - 通过资源访问文件内容

## 路径格式和工作目录

### 🔒 工作目录限制

**所有文件操作都被严格限制在工作目录内：`C:\Users\Administrator\Desktop\TS`**

- ✅ 相对路径：`file.txt`、`subfolder\file.txt` → 解析为 `C:\Users\Administrator\Desktop\TS\file.txt`
- ✅ 工作目录内的绝对路径：`C:\Users\Administrator\Desktop\TS\data\test.txt`
- ❌ 工作目录外的路径：`C:\Windows\file.txt`、`D:\other\file.txt` → **被拒绝**
- ❌ 使用 `..` 逃逸的路径：`..\..\..\Windows\file.txt` → **被拒绝**

### 路径示例

```javascript
// ✅ 允许 - 相对路径（自动解析到工作目录）
"test.txt"              → C:\Users\Administrator\Desktop\TS\test.txt
"data\file.txt"         → C:\Users\Administrator\Desktop\TS\data\file.txt
".\subfolder\file.txt"  → C:\Users\Administrator\Desktop\TS\subfolder\file.txt

// ✅ 允许 - 工作目录内的绝对路径
"C:\\Users\\Administrator\\Desktop\\TS\\file.txt"
"C:\\Users\\Administrator\\Desktop\\TS\\subfolder\\data.txt"

// ❌ 拒绝 - 超出工作目录
"C:\\Windows\\file.txt"           → 错误: Access denied
"..\\..\\other\\file.txt"         → 错误: Access denied
"C:\\Users\\Administrator\\Documents\\file.txt" → 错误: Access denied
```

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

## 运行

```bash
bun run index.ts
# 或使用 npm
npm start
```

服务将在 `http://localhost:3000/mcp` 启动。

## 环境变量

- `PORT` - 服务端口（默认: 3000）

## 使用示例

### 使用 curl 测试

#### 读取文件（完整内容）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
      "name": "read_file",
      "arguments": {
        "filePath": "test.txt"
      }
    }
  }'
```

#### 读取文件（前 100 行）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
      "name": "read_file",
      "arguments": {
        "filePath": "large-file.txt",
        "mode": "head",
        "lines": 100
      }
    }
  }'
```

#### 读取文件（后 50 行）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
      "name": "read_file",
      "arguments": {
        "filePath": "log-file.txt",
        "mode": "tail",
        "lines": 50
      }
    }
  }'
```

#### 读取文件（指定行范围）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
      "name": "read_file",
      "arguments": {
        "filePath": "code.py",
        "mode": "range",
        "startLine": 10,
        "endLine": 50
      }
    }
  }'
```

#### 写入文件（覆盖模式）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "write_file",
      "arguments": {
        "filePath": "test.txt",
        "content": "Hello, World!",
        "mode": "overwrite"
      }
    }
  }'
```

#### 写入文件（追加模式）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "write_file",
      "arguments": {
        "filePath": "log.txt",
        "content": "\nNew log entry",
        "mode": "append"
      }
    }
  }'
```

#### 写入文件（插入模式 - 在第 5 行插入）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "write_file",
      "arguments": {
        "filePath": "code.py",
        "content": "import os",
        "mode": "insert",
        "lineNumber": 5
      }
    }
  }'
```

#### 写入文件（替换模式 - 替换第 10-15 行）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "write_file",
      "arguments": {
        "filePath": "config.json",
        "content": "  \"newSetting\": true",
        "mode": "replace",
        "startLine": 10,
        "endLine": 15
      }
    }
  }'
```

#### 删除文件中的行（删除第 5-10 行）

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/call",
    "params": {
      "name": "delete_lines",
      "arguments": {
        "filePath": "data.txt",
        "startLine": 5,
        "endLine": 10
      }
    }
  }'
```

#### 列出目录

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "list_directory",
      "arguments": {
        "dirPath": "."
      }
    }
  }'
```

#### 获取文件信息

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 4,
    "method": "tools/call",
    "params": {
      "name": "file_info",
      "arguments": {
        "filePath": "package.json"
      }
    }
  }'
```

#### 获取工作目录信息

```bash
curl -X POST http://localhost:3000/mcp \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 5,
    "method": "tools/call",
    "params": {
      "name": "get_work_directory",
      "arguments": {}
    }
  }'
```

## 技术栈

- Node.js / Bun
- TypeScript
- Express.js
- Model Context Protocol SDK
- Zod (数据验证)

## 安全注意事项

⚠️ **警告**: 此服务允许文件系统访问，请确保：

- 仅在受信任的环境中运行
- 考虑添加路径验证和访问控制
- 在生产环境中添加身份验证
- 限制可访问的目录范围

## 许可证

MIT
