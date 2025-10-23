# MCP Multi-Server Client

一个用于学习的 Model Context Protocol (MCP) 多服务器客户端，支持连接多个 MCP 服务器并通过 OpenAI 兼容 API 进行对话。

## Task

- [] 添加 STDIO 之外的传输方式支持
- [] 优化架构

## 🎯 项目简介

MCP Multi-Server Client 是一个 TypeScript 实现的 MCP 客户端，允许您：

- 📡 同时连接多个 MCP 服务器
- 🤖 集成 OpenAI API（或兼容服务如 Gemini）进行智能对话
- 🛠️ 聚合多个服务器的工具，自动路由工具调用
- 💬 支持流式和非流式对话模式
- ⚡ 异步并发处理，高性能连接管理

## ✨ 核心特性

### 1. 多服务器管理

- ✅ 支持同时连接多个 MCP 服务器
- ✅ 自动聚合所有服务器的工具
- ✅ 智能工具路由（自动识别工具所属服务器）
- ✅ 统一的工具调用接口

### 2. AI 对话集成

- ✅ 集成 OpenAI API（支持 Gemini、DeepSeek 等兼容服务）
- ✅ 支持流式响应（实时输出）
- ✅ 自动工具调用和结果处理
- ✅ 对话历史管理

### 3. 灵活配置

- ✅ JSON 配置文件管理服务器
- ✅ 环境变量支持
- ✅ 可自定义 AI 模型和 API 端点

### 4. 友好交互

- ✅ 命令行交互式对话
- ✅ 详细的日志输出
- ✅ 错误处理和重试机制

## 🏗️ 系统架构

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         用户交互层                            │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              CLI (cli.ts)                            │    │
│  │  - 用户输入                                           │    │
│  │  - 流式输出                                           │    │
│  │  - 交互循环                                           │    │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                       应用层 (app.ts)                        │
│  - 初始化 MCPManager                                         │
│  - 创建 Agent                                                │
│  - 启动对话循环                                              │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                    核心业务层                                 │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Agent (agent.ts)                        │    │
│  │  ┌───────────────────────────────────────────────┐  │    │
│  │  │  - 对话管理                                    │  │    │
│  │  │  - OpenAI API 调用                            │  │    │
│  │  │  - 工具调用协调                               │  │    │
│  │  │  - 流式响应处理                               │  │    │
│  │  └───────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────┘    │
│                         │                                     │
│  ┌─────────────────────▼─────────────────────────────┐      │
│  │         MCPManager (mcp-manager.ts)               │      │
│  │  ┌───────────────────────────────────────────┐    │      │
│  │  │  - 多服务器连接管理                       │    │      │
│  │  │  - 工具聚合                               │    │      │
│  │  │  - 工具路由                               │    │      │
│  │  │  - 服务器生命周期管理                     │    │      │
│  │  └───────────────────────────────────────────┘    │      │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                      MCP 连接层                              │
│                                                               │
│  ┌─────────────────────────────────────────────────────┐    │
│  │   MCPServerConnection (mcp-server-connection.ts)    │    │
│  │  ┌───────────────────────────────────────────────┐  │    │
│  │  │  - 单服务器连接                               │  │    │
│  │  │  - STDIO 传输                                 │  │    │
│  │  │  - 工具列表/调用                              │  │    │
│  │  └───────────────────────────────────────────────┘  │    │
│  └─────────────────────────────────────────────────────┘    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                      外部服务层                              │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ MCP Server 1 │  │ MCP Server 2 │  │ MCP Server N │      │
│  │  (Python)    │  │  (Node.js)   │  │    (...)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  ┌──────────────────────────────────────────────────┐       │
│  │  OpenAI Compatible API                            │       │
│  │  (OpenAI / Gemini / DeepSeek / ...)              │       │
│  └──────────────────────────────────────────────────┘       │
└───────────────────────────────────────────────────────────────┘
```

### 工具流程图

```
用户输入
   │
   ▼
Agent.chatStream()
   │
   ├─► OpenAI API 调用
   │      │
   │      ├─► 返回文本内容 ──► 流式输出给用户
   │      │
   │      └─► 返回工具调用请求
   │             │
   │             ▼
   │      Agent.executeToolCalls()
   │             │
   │             ▼
   │      MCPManager.callTool(toolName, args)
   │             │
   │             ├─► 查找工具所属服务器（toolToServerMap）
   │             │
   │             ▼
   │      MCPServerConnection.callTool()
   │             │
   │             ▼
   │      MCP Server 执行工具
   │             │
   │             ▼
   │      返回工具结果
   │             │
   │             ▼
   │      添加到对话历史
   │             │
   └─────────────┘
   │
   ▼
再次调用 OpenAI API（带工具结果）
   │
   ▼
继续对话...
```

### 数据流图

```
┌──────────────┐
│ config.json  │
└──────┬───────┘
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│ 配置加载 (config-loader.ts)                               │
│  - loadMCPConfig()                                        │
│  - 验证配置                                               │
└──────┬───────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│ MCPManager 初始化                                         │
│  1. 解析服务器配置                                        │
│  2. 并发连接所有服务器                                    │
│  3. 加载所有工具                                          │
│  4. 构建工具路由映射                                      │
└──────┬───────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│ Agent 创建                                                │
│  - 初始化 OpenAI 客户端                                   │
│  - 设置模型                                               │
│  - 准备对话历史                                           │
└──────┬───────────────────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│ 对话循环                                                  │
│  ┌────────────────────────────────────────────────────┐  │
│  │ 1. 用户输入 ──► 对话历史                          │  │
│  │ 2. OpenAI API 调用（附带所有可用工具）            │  │
│  │ 3. 处理响应：                                      │  │
│  │    ├─► 文本内容 ──► 输出给用户                   │  │
│  │    └─► 工具调用 ──► MCPManager ──► 对话历史      │  │
│  │ 4. 循环直到无工具调用                              │  │
│  └────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 前置要求

- Node.js >= 18.0.0
- 可用的 MCP 服务器（如 filesystem、fetch 等）
- OpenAI API 密钥或兼容服务

### 安装

```bash
# 安装依赖
npm install
# 或使用 bun
bun install
```

### 配置

1. **创建配置文件 `config.json`**

```json
{
  "servers": {
    "filesystem": {
      "type": "stdio",
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "/path/to/directory"
      ]
    },
    "fetch": {
      "type": "stdio",
      "command": "uvx",
      "args": ["mcp-server-fetch"]
    }
  }
}
```

2. **创建环境变量文件 `.env`**

```env
OPENAI_BASE_URL=http://your-api-endpoint/v1
OPENAI_API_KEY=your-api-key
OPENAI_MODEL=gemini-2.5-flash-preview-05-20
```

### 运行

```bash
# 启动应用
npm start
# 或使用 bun
bun run app.ts
```

### 测试

```bash
👤 You: Test all the tools you have to see if they work

🤖 Assistant:    ⏳ Calling tool: get_current_time...
🔧 [time] Calling tool: get_current_time {"timezone":"America/New_York"}
   ✓ Tool result: {
  "timezone": "America/New_York",
  "datetime": "2025-10-23T00:52:52-04:00",
  "day_of_week": "Thursday",
  "is_dst": true
}
====================================================================================================
   ⏳ Calling tool: convert_time...
🔧 [time] Calling tool: convert_time {"target_timezone":"Europe/London","time":"10:30","source_timezone":"America/New_York"}
   ✓ Tool result: {
  "source": {
    "timezone": "America/New_York",
    "datetime": "2025-10-23T10:30:00-04:00",
    "day_of_week": "Thursday",
    "is_dst": true
  },
  "target": {
    "timezone": "Europe/London",
 ...
====================================================================================================
   ⏳ Calling tool: fetch...
🔧 [fetch] Calling tool: fetch {"url":"https://www.google.com"}
Warning: A working NPM installation was not found. The package will use Python-based article extraction.
Warning: node executable not found, reverting to pure-Python mode. Install Node.js v10 or newer to use Readability.js.
   ✓ Tool result: Contents of https://www.google.com/:
Google

搜尋 圖片 地圖 Play YouTube 新聞 Gmail 雲端硬碟 更多 »

網頁記錄 | 設定 | 登入

Google 透過以下語言提供: 简体中文 English

廣告關於 GoogleGoogle.com.hk

© 2025 - 私隱權政策 - 條款
====================================================================================================
All tools are working as expected!

*   `get_current_time`: This tool can fetch the current time for a specified timezone.
*   `convert_time`: This tool can convert a given time from one timezone to another.
*   `fetch`: This tool can fetch content from a given URL on the internet.


👤 You:
```

## ⚙️ 配置说明

### config.json 结构

```typescript
{
  "servers": {
    "server-name": {
      "type": "stdio",           // 传输类型（目前仅支持 stdio）
      "command": "string",       // 启动命令
      "args": ["string"],        // 命令参数
      "env": {                   // 可选：环境变量
        "KEY": "value"
      }
    }
  }
}
```

## 📁 项目结构

```
demo-mcp-client/
├── src/
│   ├── mcp/                          # MCP 核心模块
│   │   ├── mcp-manager.ts           # 多服务器管理器
│   │   └── mcp-server-connection.ts # 单服务器连接
│   │
│   ├── utils/                        # 工具函数
│   │   ├── cli.ts                   # 命令行交互工具
│   │   ├── config-loader.ts         # 配置加载器
│   │   └── openai-integration.ts    # OpenAI 集成工具
│   │
│   ├── types/                        # 类型定义
│   │   └── index.ts                 # 所有类型导出
│   │
│   ├── agent.ts                      # AI 代理
│   └── index.ts                      # 模块导出
│
├── app.ts                            # 应用入口
├── config.json                       # MCP 服务器配置
├── .env                              # 环境变量（需自行创建）
├── package.json                      # 项目配置
├── tsconfig.json                     # TypeScript 配置
└── README.md                         # 项目文档
```
