# Demo Meeting UI

🎥 一个基于 Vue 3 + TypeScript + WebRTC 的现代化视频会议系统前端

## Task

- [ ] 迁移到 ElementUI

## 🚀 项目简介

Demo Meeting UI 是一个功能完整的视频会议系统前端应用，支持多人实时音视频通话、屏幕共享、实时聊天等功能。项目采用现代化的前端技术栈，具有高性能、易扩展、易维护的特点。

### 主要特性

- ✅ **实时音视频通话**：基于 WebRTC P2P 技术
- ✅ **屏幕共享**：支持共享整个屏幕、应用窗口或特定标签页
- ✅ **实时聊天**：支持文本消息和系统事件通知
- ✅ **NAT 穿透**：集成 COTURN 服务器支持极端网络环境
- ✅ **会议管理**：快速会议、预约会议、加入会议
- ✅ **响应式设计**：适配不同屏幕尺寸
- ✅ **Web Worker**：WebSocket 在独立线程中运行，不阻塞主线程

## 🛠 技术栈

### 核心框架

- **Vue 3.5** - 渐进式 JavaScript 框架（Composition API）
- **TypeScript 5.9** - JavaScript 的超集，提供类型安全
- **Vite 7** - 下一代前端构建工具

### UI 组件库

- **Element Plus 2.9** - 基于 Vue 3 的组件库
- **Element Plus Icons** - 图标组件

### 状态管理 & 路由

- **Pinia 3.0** - Vue 3 官方推荐的状态管理库
- **Vue Router 4.5** - Vue 官方路由管理器

### 网络通信

- **Axios 1.12** - HTTP 客户端
- **WebSocket** - 实时双向通信（基于 Web Worker）
- **Simple Peer 9.11** - WebRTC 封装库

### 开发工具

- **Sass** - CSS 预处理器
- **Prettier** - 代码格式化工具
- **Vue DevTools** - Vue 开发者工具

## 🏗 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      Browser Layer                       │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────────┐ │
│  │   Views    │  │  Components │  │   Router + Store │ │
│  └────────────┘  └─────────────┘  └──────────────────┘ │
└────────────┬────────────────────────────────┬───────────┘
             │                                │
             ▼                                ▼
┌────────────────────────┐      ┌────────────────────────┐
│     Utils Layer        │      │    API Layer           │
│  ┌──────────────────┐  │      │  ┌──────────────────┐  │
│  │ WebRTC Manager   │  │      │  │  HTTP Client     │  │
│  │  (Simple Peer)   │  │      │  │   (Axios)        │  │
│  └──────────────────┘  │      │  └──────────────────┘  │
│  ┌──────────────────┐  │      └────────────────────────┘
│  │ WebSocket Worker │  │
│  │  (Web Worker)    │  │
│  └──────────────────┘  │
│  ┌──────────────────┐  │
│  │ Track Manager    │  │
│  │ (Media Control)  │  │
│  └──────────────────┘  │
└────────────┬───────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                    Network Layer                         │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐ │
│  │  WebSocket  │  │   WebRTC     │  │   HTTP/REST    │ │
│  │  (Signaling)│  │   (P2P)      │  │   (API)        │ │
│  └─────────────┘  └──────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                    Backend Services                      │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐ │
│  │   Signaling │  │    COTURN    │  │   REST API     │ │
│  │   Server    │  │   (TURN/STUN)│  │   Server       │ │
│  └─────────────┘  └──────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 通信流程

#### WebRTC 连接建立流程

```
User A                    Signaling Server           User B
  │                              │                      │
  │────① Create Offer───────────>│                      │
  │                              │──② Forward Offer────>│
  │                              │                      │
  │                              │<──③ Create Answer────│
  │<────④ Forward Answer─────────│                      │
  │                              │                      │
  │─────────────⑤ ICE Candidates Exchange──────────────>│
  │                              │                      │
  │<════════⑥ P2P Connection (Media Stream)════════════>│
```

#### 消息流转架构

```
┌─────────────────────────────────────────────────────────┐
│                    Main Thread                           │
│  ┌──────────────┐         ┌────────────────────────┐   │
│  │  Components  │────────>│  WebSocketManager      │   │
│  │              │<────────│  (Event Subscribers)   │   │
│  └──────────────┘         └────────────────────────┘   │
│         │                            │                  │
│         │                            │ postMessage      │
│         ▼                            ▼                  │
└─────────────────────────────────────────────────────────┘
                                       │
                                       │
┌──────────────────────────────────────┼──────────────────┐
│                    Worker Thread     │                  │
│                                      ▼                  │
│         ┌────────────────────────────────────────┐     │
│         │      WebSocket Worker                  │     │
│         │  ┌──────────────────────────────────┐  │     │
│         │  │   WebSocket Connection           │  │     │
│         │  │   - Send/Receive Messages        │  │     │
│         │  │   - Auto Reconnect               │  │     │
│         │  │   - Heartbeat                    │  │     │
│         │  └──────────────────────────────────┘  │     │
│         └────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────┘
```

## 📁 目录结构

```
demo-meeting-ui/
├── public/                      # 静态资源目录
├── src/
│   ├── api/                     # API 接口层
│   │   ├── index.ts            # API 请求封装
│   │   ├── dto.d.ts            # 数据传输对象类型定义
│   │   ├── vo.d.ts             # 视图对象类型定义
│   │   └── typings.d.ts        # API 相关类型定义
│   │
│   ├── assets/                  # 资源文件
│   │   └── style.scss          # 全局样式
│   │
│   ├── components/              # 公共组件
│   │   ├── ChatPanel.vue       # 聊天面板（支持消息和事件）
│   │   ├── VideoTile.vue       # 视频画面组件
│   │   ├── ShareScreenButton.vue      # 屏幕共享控制
│   │   ├── JoinMeetingButton.vue      # 加入会议按钮
│   │   ├── QuickMeetingButton.vue     # 快速会议按钮
│   │   └── ScheduleMeetingButton.vue  # 预约会议按钮
│   │
│   ├── router/                  # 路由配置
│   │   └── index.ts            # 路由定义和守卫
│   │
│   ├── stores/                  # Pinia 状态管理
│   │   └── user.ts             # 用户状态管理
│   │
│   ├── utils/                   # 工具函数
│   │   ├── rtc/                # WebRTC 相关
│   │   │   ├── index.ts        # WebRTC 管理器
│   │   │   └── rtc.config.ts   # RTC 配置（ICE 服务器）
│   │   │
│   │   ├── track/              # 媒体轨道管理
│   │   │   └── index.ts        # 音视频设备控制
│   │   │
│   │   └── websocket/          # WebSocket 相关
│   │       ├── index.ts        # WebSocket 管理器
│   │       ├── websocket.types.ts     # 类型定义
│   │       └── websocket.worker.ts    # Web Worker 实现
│   │
│   ├── views/                   # 页面组件
│   │   ├── Login.vue           # 登录页
│   │   ├── Dashboard.vue       # 控制台
│   │   └── MeetingRoom.vue     # 会议室
│   │
│   ├── App.vue                  # 根组件
│   └── main.ts                  # 应用入口
│
├── docs/                        # 文档目录
│   └── COTURN_SETUP.md         # COTURN 配置文档
│
├── index.html                   # HTML 入口
├── package.json                 # 项目配置
├── tsconfig.json               # TypeScript 配置
├── vite.config.ts              # Vite 配置
└── README.md                    # 项目说明
```

## COTURN 中继支持

在极端 NAT 环境下通过 TURN 服务器中转流量：

- 配置化的 ICE 服务器
- 支持 UDP/TCP 协议
- 自动 fallback 机制

**配置文件**: `src/utils/rtc/rtc.config.ts`

## 🚀 快速开始

### 安装依赖

```bash
# 使用 npm
npm install

# 或使用 yarn
yarn install

# 或使用 pnpm
pnpm install

# 或使用 bun
bun install
```

### 开发模式

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动

### 环境变量

创建 `.env` 文件：

```env
# API 基础地址
VITE_API_BASE_URL=http://your-api-server.com

# WebSocket 地址
VITE_WS_URL=ws://your-signaling-server.com

# COTURN 服务器地址
VITE_TURN_SERVER=192.168.31.3:3478
```

### RTC 配置

编辑 `src/utils/rtc/rtc.config.ts`:

```typescript
export const defaultRTCConfig = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    {
      urls: ['turn:192.168.31.3:3478?transport=udp'],
      username: 'your-username',
      credential: 'your-password',
    },
  ],
}
```
