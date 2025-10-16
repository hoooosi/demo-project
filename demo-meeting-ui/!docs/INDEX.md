# 📚 WebRTC 视频会议系统 - 完整文档索引

## 🎯 项目概览

这是一个基于 **WebRTC**、**Netty** 和 **Vue 3** 实现的完整视频会议解决方案。

### 核心特性
- ✅ 多人实时视频通话
- ✅ Netty 实现的高性能信令服务器
- ✅ Vue 3 + Element Plus 现代化前端界面
- ✅ P2P 直连，低延迟
- ✅ 完整的房间管理系统
- ✅ 音视频控制功能

## 📖 文档导航

### 1. [快速开始 (QUICK_START.md)](./QUICK_START.md)
**推荐首先阅读**

适合：想要快速运行系统的用户

内容：
- 一键启动脚本使用
- 基本使用步骤
- 常见问题解答
- 浏览器兼容性

### 2. [完整文档 (WEBRTC_README.md)](./WEBRTC_README.md)
**详细的系统说明**

适合：需要深入了解系统的开发者

内容：
- 系统架构详解
- 技术栈说明
- API 接口文档
- 信令协议规范
- 核心代码实现
- 故障排查指南

### 3. [配置说明 (CONFIGURATION.md)](./CONFIGURATION.md)
**生产环境配置指南**

适合：需要部署到生产环境的运维人员

内容：
- 后端配置详解
- 前端配置详解
- TURN 服务器配置
- HTTPS/WSS 配置
- Nginx 反向代理
- 性能优化建议
- 监控和日志

## 🗂️ 项目结构

```
demo-project/
├── demo-meeting/              # 后端项目 (Spring Boot + Netty)
│   └── src/main/java/io/github/hoooosi/meeting/
│       ├── controller/        # REST API 控制器
│       ├── websocket/         # WebSocket 相关
│       │   ├── webrtc/       # WebRTC 信令服务器
│       │   │   ├── WebRTCSignalingServer.java    # 信令服务器启动器
│       │   │   ├── WebRTCSignalHandler.java      # 信令消息处理器
│       │   │   └── WebRTCRoomManager.java        # 房间管理器
│       │   └── message/       # 消息模型
│       │       └── webrtc/   # WebRTC 消息定义
│       └── config/           # 配置类
│
├── demo-meeting-ui/           # 前端项目 (Vue 3 + Vite)
│   ├── src/
│   │   ├── views/            # 页面组件
│   │   │   ├── Home.vue             # 首页（加入房间）
│   │   │   └── VideoCall.vue        # 视频通话页面
│   │   ├── utils/            # 工具类
│   │   │   ├── webrtc-signaling.js  # 信令客户端
│   │   │   └── webrtc-manager.js    # WebRTC 连接管理
│   │   └── router/           # 路由配置
│   └── public/
│       └── test-signaling.html      # 信令测试页面
│
├── start.sh                   # Linux/Mac 启动脚本
├── start.ps1                  # Windows 启动脚本
├── QUICK_START.md            # 快速开始指南
├── WEBRTC_README.md          # 完整文档
├── CONFIGURATION.md          # 配置说明
└── INDEX.md                  # 本文档
```

## 🚀 快速开始

### 最快的方式

**Windows:**
```powershell
.\start.ps1
```

**Linux/Mac:**
```bash
chmod +x start.sh
./start.sh
```

### 手动启动

1. **启动后端**
```bash
cd demo-meeting
mvn clean package -DskipTests
java -jar target/meeting-1.0-SNAPSHOT.jar
```

2. **启动前端**（新终端）
```bash
cd demo-meeting-ui
bun install  # 或 npm install
bun run dev  # 或 npm run dev
```

3. **访问应用**
- 前端界面: http://localhost:5173
- 测试页面: http://localhost:5173/test-signaling.html
- API 文档: http://localhost:8080/doc.html

## 🔗 关键端口

| 端口 | 服务 | 说明 |
|------|------|------|
| 8080 | REST API | Spring Boot HTTP 服务 |
| 8090 | **WebRTC 信令** | **Netty WebSocket 信令服务器** |
| 5173 | 前端开发服务器 | Vite 开发服务器 |

## 📋 使用流程

```
1. 打开首页 (http://localhost:5173)
   ↓
2. 输入用户名和房间ID
   ↓
3. 点击"加入房间"
   ↓
4. 授权摄像头和麦克风
   ↓
5. 进入视频通话界面
   ↓
6. 邀请其他人加入相同房间ID
   ↓
7. 自动建立 P2P 视频连接
```

## 🏗️ 技术架构

### 信令流程

```
客户端A                    Netty信令服务器                  客户端B
  │                              │                            │
  │─────── join ─────────────────→                           │
  │                              │                            │
  │←──── room_users ─────────────│                           │
  │                              │                            │
  │                              │←────── join ──────────────│
  │                              │                            │
  │←──── user_joined ────────────│──── room_users ──────────→│
  │                              │                            │
  │─────── offer ───────────────→│                           │
  │                              │─────── offer ────────────→│
  │                              │                            │
  │                              │←───── answer ─────────────│
  │←───── answer ────────────────│                           │
  │                              │                            │
  │─── ice_candidate ───────────→│                           │
  │                              │─── ice_candidate ────────→│
  │                              │                            │
  │                         [P2P连接建立]                     │
  │═══════════════════════════════════════════════════════════│
  │              直接的音视频流传输 (不经过服务器)              │
  │═══════════════════════════════════════════════════════════│
```

### 核心组件

#### 后端
1. **WebRTCSignalingServer** - Netty 服务器启动器
2. **WebRTCSignalHandler** - 处理所有信令消息
3. **WebRTCRoomManager** - 管理房间和用户状态

#### 前端
1. **WebRTCSignalingClient** - WebSocket 客户端
2. **WebRTCManager** - RTCPeerConnection 管理
3. **VideoCall.vue** - 视频通话 UI 组件

## 🧪 测试工具

### 信令测试页面
访问: http://localhost:5173/test-signaling.html

功能：
- 测试 WebSocket 连接
- 发送各种信令消息
- 查看服务器响应
- 实时查看消息日志
- 自定义消息测试

## 📚 学习资源

### WebRTC 基础
- [WebRTC 官方文档](https://webrtc.org/)
- [MDN WebRTC API](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)

### Netty
- [Netty 官方文档](https://netty.io/)
- [Netty WebSocket 示例](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/websocketx)

### Vue 3
- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)

## 🐛 常见问题

### 1. 编译失败
```bash
# 清理并重新编译
cd demo-meeting
mvn clean
mvn package -DskipTests
```

### 2. 端口被占用
```bash
# Windows 查找并关闭占用端口的进程
netstat -ano | findstr :8090
taskkill /PID <进程ID> /F

# Linux/Mac
lsof -ti:8090 | xargs kill -9
```

### 3. WebRTC 连接失败
- 检查防火墙设置
- 确保 STUN 服务器可访问
- 考虑配置 TURN 服务器（复杂网络环境）

### 4. 看不到视频
- 检查摄像头权限
- 确保使用支持 WebRTC 的浏览器
- 打开浏览器控制台查看错误

## 🔐 安全建议

### 生产环境必须配置：

1. **启用 HTTPS/WSS**
   - 获取 SSL 证书（Let's Encrypt 免费）
   - 配置 Nginx 反向代理

2. **添加身份认证**
   - JWT Token 验证
   - 房间密码保护

3. **限流和防护**
   - 限制同时连接数
   - 防止恶意创建房间
   - 添加速率限制

4. **数据保护**
   - 启用 DTLS 和 SRTP
   - 敏感信息加密存储

## 📈 性能指标

### 推荐配置
- **2-4 人会议**: 1 核 2GB 内存
- **5-10 人会议**: 2 核 4GB 内存
- **10+ 人会议**: 4 核 8GB 内存

### 带宽需求
- **视频**: 1-3 Mbps (720p)
- **音频**: 50-100 Kbps
- **信令**: < 10 Kbps

## 🎯 下一步

1. ✅ **运行项目**: 使用快速开始指南
2. 📖 **了解架构**: 阅读完整文档
3. ⚙️ **自定义配置**: 参考配置说明
4. 🚀 **部署上线**: 配置生产环境
5. 🔧 **功能扩展**: 添加新功能

## 💬 获取帮助

- 查看文档: 本项目提供的所有 .md 文件
- 查看日志: 后端日志和浏览器控制台
- 使用测试工具: test-signaling.html
- 提交 Issue: 报告问题和建议

## 📝 版本信息

- **Spring Boot**: 3.4.5
- **Netty**: 4.1.100.Final
- **Vue**: 3.5.22
- **Element Plus**: 2.9.1
- **Java**: 17+
- **Node.js**: 20+

---

**开始你的视频会议之旅! 🎉**

[快速开始](./QUICK_START.md) | [完整文档](./WEBRTC_README.md) | [配置说明](./CONFIGURATION.md)
