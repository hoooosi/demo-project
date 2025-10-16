# WebRTC 视频会议系统

基于 **WebRTC**、**Netty** 和 **Vue 3** 实现的实时视频会议系统。

## 🚀 功能特性

- ✅ **多人视频通话** - 支持多人同时进行视频通话
- ✅ **音频/视频控制** - 可以随时开关麦克风和摄像头
- ✅ **实时用户列表** - 显示房间内所有参与者
- ✅ **自动重连** - 网络断开时自动重新连接
- ✅ **响应式设计** - 支持桌面和移动设备
- ✅ **房间管理** - 可以创建和加入不同的房间

## 🏗️ 技术栈

### 后端
- **Spring Boot 3.4.5** - Java后端框架
- **Netty 4.1.100** - 高性能网络框架
- **WebSocket** - 实时双向通信
- **MyBatis Plus** - 数据库ORM
- **PostgreSQL** - 关系型数据库

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **Element Plus** - UI组件库
- **WebRTC API** - 浏览器原生视频通话API
- **Vite** - 前端构建工具

## 📋 系统架构

```
┌─────────────┐         WebSocket         ┌──────────────────┐
│   浏览器A    │ ◄─────────────────────► │                  │
│  (WebRTC)   │                          │  Netty信令服务器  │
└─────────────┘                          │   (Port 8090)    │
       │                                 │                  │
       │        P2P Video/Audio          └──────────────────┘
       │       (直接连接)                          │
       ▼                                          │
┌─────────────┐         WebSocket                │
│   浏览器B    │ ◄───────────────────────────────┘
│  (WebRTC)   │
└─────────────┘
```

## 🔧 安装和运行

### 前置要求

- **JDK 17+**
- **Node.js 20+** 或 **Bun**
- **Maven 3.6+**
- **PostgreSQL 14+** (可选，如果不需要持久化可以注释掉)

### 1. 启动后端

```bash
# 进入后端目录
cd demo-meeting

# 编译项目
mvn clean package -DskipTests

# 运行应用
java -jar target/meeting-1.0-SNAPSHOT.jar
```

后端服务将启动在以下端口：
- **8080** - REST API 端口
- **8089** - WebSocket 端口 (原有的)
- **8090** - WebRTC 信令服务器端口

### 2. 启动前端

```bash
# 进入前端目录
cd demo-meeting-ui

# 安装依赖 (使用 bun 或 npm)
bun install
# 或
npm install

# 启动开发服务器
bun run dev
# 或
npm run dev
```

前端将运行在 **http://localhost:5173**

## 📖 使用说明

### 1. 访问应用

打开浏览器访问：`http://localhost:5173`

### 2. 创建或加入房间

- 输入你的**用户名**
- 输入**房间ID**（或点击"创建随机房间"按钮）
- 点击"加入房间"按钮

### 3. 授权摄像头和麦克风

- 浏览器会请求访问你的摄像头和麦克风权限
- 点击"允许"授权访问

### 4. 开始视频通话

- 进入房间后，你可以看到自己的视频
- 其他用户加入后，会自动建立P2P连接
- 可以随时使用底部控制栏开关音视频

### 5. 控制功能

- 🎤 **麦克风按钮** - 开关麦克风
- 📹 **摄像头按钮** - 开关摄像头
- 📞 **挂断按钮** - 离开房间

## 🔌 API 接口

### REST API

#### 获取所有房间
```http
GET http://localhost:8080/api/webrtc/rooms
```

#### 获取房间用户列表
```http
GET http://localhost:8080/api/webrtc/rooms/{roomId}/users
```

#### 健康检查
```http
GET http://localhost:8080/api/webrtc/health
```

### WebSocket 信令协议

连接地址：`ws://localhost:8090/webrtc`

#### 加入房间
```json
{
  "type": "join",
  "roomId": "room-123",
  "userId": 10001,
  "username": "张三"
}
```

#### 离开房间
```json
{
  "type": "leave",
  "roomId": "room-123",
  "userId": 10001
}
```

#### 发送 Offer
```json
{
  "type": "offer",
  "roomId": "room-123",
  "fromUserId": 10001,
  "toUserId": 10002,
  "sdp": { ... }
}
```

#### 发送 Answer
```json
{
  "type": "answer",
  "roomId": "room-123",
  "fromUserId": 10002,
  "toUserId": 10001,
  "sdp": { ... }
}
```

#### 发送 ICE Candidate
```json
{
  "type": "ice_candidate",
  "roomId": "room-123",
  "fromUserId": 10001,
  "toUserId": 10002,
  "candidate": { ... }
}
```

## 🎯 核心实现

### 后端 - Netty 信令服务器

**WebRTCSignalingServer.java** - 独立的WebRTC信令服务器
- 运行在 8090 端口
- 处理 WebSocket 连接
- 转发信令消息

**WebRTCSignalHandler.java** - 信令消息处理器
- 处理 join/leave/offer/answer/ice_candidate 消息
- 管理房间和用户状态
- 转发 P2P 信令

**WebRTCRoomManager.java** - 房间管理器
- 管理房间和用户映射
- 跟踪用户连接状态
- 广播房间消息

### 前端 - WebRTC 客户端

**webrtc-signaling.js** - 信令客户端
- WebSocket 连接管理
- 消息发送和接收
- 自动重连机制

**webrtc-manager.js** - WebRTC 连接管理
- RTCPeerConnection 管理
- Offer/Answer 交换
- ICE 候选收集和交换
- 媒体流管理

**VideoCall.vue** - 视频通话页面
- 本地和远程视频显示
- 音视频控制
- 用户列表管理

## 🔍 故障排查

### 1. 无法访问摄像头/麦克风

- 确保浏览器已授权摄像头和麦克风权限
- 检查是否有其他应用正在使用摄像头
- 使用 HTTPS 或 localhost（HTTP 在某些浏览器上会被限制）

### 2. 视频连接失败

- 检查防火墙设置
- 确保 STUN 服务器可访问
- 如果在 NAT 环境下，可能需要配置 TURN 服务器

### 3. 信令服务器连接失败

- 确保后端服务已启动
- 检查 8090 端口是否被占用
- 查看浏览器控制台错误信息

### 4. 编译错误

- 确保使用 JDK 17 或更高版本
- 清理 Maven 缓存：`mvn clean`
- 重新安装前端依赖：`rm -rf node_modules && bun install`

## 📝 配置说明

### 修改端口

**后端 - application.yml**
```yaml
server:
  port: 8080  # REST API 端口
```

**WebRTCSignalingServer.java**
```java
private static final int WEBRTC_PORT = 8090;  // 信令服务器端口
```

**前端 - VideoCall.vue**
```javascript
const wsUrl = `ws://localhost:8090/webrtc`;  // 信令服务器地址
```

### 配置 TURN 服务器

如果需要在复杂网络环境下使用，可以配置 TURN 服务器：

**webrtc-manager.js**
```javascript
this.configuration = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { 
      urls: 'turn:your-turn-server.com:3478',
      username: 'your-username',
      credential: 'your-password'
    }
  ]
};
```

## 🌟 未来改进

- [ ] 添加屏幕共享功能
- [ ] 实现录制功能
- [ ] 添加聊天功能
- [ ] 美颜滤镜
- [ ] 虚拟背景
- [ ] 会议录制和回放
- [ ] 用户权限管理
- [ ] 房间加密

## 📄 许可证

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题，请提交 Issue 或发送邮件。

---

**Enjoy your video calling! 🎉**
