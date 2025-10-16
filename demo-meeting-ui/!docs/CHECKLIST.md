# 🚦 WebRTC 视频会议系统 - 启动检查清单

## ✅ 已完成的实现

### 后端 (Java + Netty)
- [x] WebRTC 信令服务器 (Netty, Port 8090)
- [x] 信令消息处理器
- [x] 房间管理系统
- [x] REST API 接口
- [x] CORS 跨域配置
- [x] 消息模型定义

### 前端 (Vue 3)
- [x] WebSocket 信令客户端
- [x] WebRTC 连接管理器
- [x] 首页 UI (Home.vue)
- [x] 视频通话页面 (VideoCall.vue)
- [x] 路由配置
- [x] Element Plus 集成

### 文档
- [x] 完整使用文档 (WEBRTC_README.md)
- [x] 快速开始指南 (QUICK_START.md)
- [x] 配置说明 (CONFIGURATION.md)
- [x] 文档索引 (INDEX.md)
- [x] 项目总结 (PROJECT_SUMMARY.md)

### 工具
- [x] 信令测试页面 (test-signaling.html)
- [x] Windows 启动脚本 (start.ps1)
- [x] Linux/Mac 启动脚本 (start.sh)

---

## 🎯 启动前检查

### 1. 环境检查
```powershell
# 检查 Java 版本 (需要 17+)
java -version

# 检查 Bun/Node.js
bun --version
# 或
node --version

# 检查 Maven
mvn --version
```

### 2. 依赖检查
- [x] 后端 pom.xml 包含 Netty 依赖 (4.1.100.Final)
- [x] 前端 package.json 包含 Element Plus
- [x] 前端依赖已安装 (您已经运行 `bun install`)

---

## 🚀 启动步骤

### 方式一：使用启动脚本（推荐）

**Windows:**
```powershell
cd e:\workspace\demo-project
.\start.ps1
```

这个脚本会自动：
1. ✅ 检查 Java 环境
2. ✅ 检查 Node.js/Bun 环境
3. ✅ 编译后端项目
4. ✅ 启动后端服务
5. ✅ 安装前端依赖（如需要）
6. ✅ 启动前端开发服务器

### 方式二：手动启动

**步骤 1: 启动后端**
```powershell
cd e:\workspace\demo-project\demo-meeting
mvn clean package -DskipTests
java -jar target/meeting-1.0-SNAPSHOT.jar
```

等待看到这些日志：
```
WebRTC Signaling Server started successfully!
Port: 8090
WebSocket Path: /webrtc
Full URL: ws://localhost:8090/webrtc
```

**步骤 2: 启动前端（新终端）**
```powershell
cd e:\workspace\demo-project\demo-meeting-ui
bun run dev
```

等待看到：
```
VITE ready in XXX ms
➜  Local:   http://localhost:5173/
```

---

## 🌐 访问应用

启动成功后，可以访问：

| 服务 | URL | 说明 |
|------|-----|------|
| 🏠 主页 | http://localhost:5173 | 加入房间页面 |
| 🧪 测试工具 | http://localhost:5173/test-signaling.html | 信令测试页面 |
| 📚 API 文档 | http://localhost:8080/doc.html | Swagger API 文档 |
| 🔍 健康检查 | http://localhost:8080/api/webrtc/health | 服务状态检查 |

---

## 🧪 测试流程

### 测试 1: 信令服务器测试

1. 打开测试页面: http://localhost:5173/test-signaling.html
2. 点击"连接服务器"
3. 应该看到: `✅ WebSocket 连接成功!`
4. 点击"加入房间"
5. 应该收到: `room_users` 消息

### 测试 2: 视频通话测试（单人）

1. 打开主页: http://localhost:5173
2. 输入用户名: `测试用户`
3. 输入房间ID: `test-room`（或点击"创建随机房间"）
4. 点击"加入房间"
5. 授权摄像头和麦克风
6. 应该看到自己的视频

### 测试 3: 视频通话测试（多人）

**方法 A: 同一台电脑**
1. 在 Chrome 中打开: http://localhost:5173
   - 用户名: `用户A`
   - 房间ID: `room-123`
2. 在另一个浏览器（如 Firefox）中打开: http://localhost:5173
   - 用户名: `用户B`
   - 房间ID: `room-123`（相同）
3. 两个浏览器应该能看到对方的视频

**方法 B: 不同电脑（局域网）**
1. 找到服务器电脑的 IP 地址（如 192.168.1.100）
2. 修改前端 VideoCall.vue 中的 WebSocket 地址：
   ```javascript
   const wsUrl = `ws://192.168.1.100:8090/webrtc`;
   ```
3. 在其他电脑访问: http://192.168.1.100:5173

---

## 🔍 验证清单

启动后，请验证以下端口是否正常监听：

### Windows 检查端口
```powershell
# 检查 8080 端口 (REST API)
netstat -ano | findstr :8080

# 检查 8090 端口 (WebRTC 信令)
netstat -ano | findstr :8090

# 检查 5173 端口 (前端)
netstat -ano | findstr :5173
```

### 预期结果
```
TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    XXXX
TCP    0.0.0.0:8090    0.0.0.0:0    LISTENING    XXXX
TCP    0.0.0.0:5173    0.0.0.0:0    LISTENING    XXXX
```

---

## ✅ 功能验证

### 基础功能
- [ ] WebSocket 连接成功
- [ ] 加入房间成功
- [ ] 本地视频显示正常
- [ ] 远程视频显示正常（多人时）
- [ ] 音频传输正常
- [ ] 麦克风开关正常
- [ ] 摄像头开关正常
- [ ] 离开房间正常

### 高级功能
- [ ] 多人同时通话（3+人）
- [ ] 断线重连
- [ ] 房间列表 API
- [ ] 用户列表更新
- [ ] 信令消息转发

---

## 🐛 常见问题排查

### 问题 1: 后端启动失败

**症状**: 编译错误或启动异常

**解决**:
```powershell
cd demo-meeting
mvn clean
mvn package -DskipTests -X  # -X 显示详细日志
```

### 问题 2: 前端连接失败

**症状**: `WebSocket connection failed`

**检查**:
1. 后端是否已启动？
2. 8090 端口是否被占用？
3. 浏览器控制台有什么错误？

**解决**:
```powershell
# 查看占用 8090 的进程
netstat -ano | findstr :8090
# 如果被占用，终止进程
taskkill /PID <进程ID> /F
```

### 问题 3: 看不到视频

**症状**: 黑屏或提示摄像头错误

**检查**:
1. 浏览器是否授权了摄像头和麦克风？
2. 设备是否被其他应用占用？
3. 是否使用了支持的浏览器？

**解决**:
- 在浏览器地址栏点击"🔒"图标
- 检查"摄像头"和"麦克风"权限
- 设置为"允许"
- 刷新页面

### 问题 4: P2P 连接失败

**症状**: 无法看到对方视频，但能加入房间

**检查**:
1. 浏览器控制台的 ICE 连接状态
2. 网络环境（防火墙、NAT 类型）

**解决**:
- 在同一台电脑的不同浏览器测试
- 检查防火墙设置
- 考虑配置 TURN 服务器（复杂网络环境）

---

## 📊 日志查看

### 后端日志
在启动后端的终端窗口查看：
- WebSocket 连接事件
- 信令消息转发
- 房间和用户状态

### 前端日志
按 F12 打开浏览器开发者工具，查看 Console：
- WebSocket 连接状态
- WebRTC 连接状态
- ICE 候选信息
- 错误信息

---

## 🎓 推荐的测试顺序

1. ✅ **信令测试** (test-signaling.html)
   - 验证 WebSocket 连接
   - 测试各种信令消息

2. ✅ **单人测试** (一个浏览器)
   - 验证摄像头和麦克风
   - 验证本地视频显示

3. ✅ **双人测试** (两个浏览器)
   - 验证 P2P 连接
   - 验证视频和音频传输

4. ✅ **多人测试** (三个或更多)
   - 验证多对等连接
   - 验证性能和稳定性

---

## 🎉 成功标志

当您看到以下现象时，说明系统运行正常：

1. ✅ 后端启动看到: `WebRTC Signaling Server started successfully!`
2. ✅ 前端启动看到: `Local: http://localhost:5173/`
3. ✅ 测试页面能成功连接
4. ✅ 主页能正常显示
5. ✅ 能看到自己的视频
6. ✅ 多人时能看到对方的视频
7. ✅ 能听到对方的声音
8. ✅ 控制按钮正常工作

---

## 📖 下一步

系统启动成功后：

1. 📚 **阅读文档**: 查看 [INDEX.md](./INDEX.md) 了解更多
2. 🎨 **自定义界面**: 修改 Vue 组件样式
3. ⚙️ **调整配置**: 参考 [CONFIGURATION.md](./CONFIGURATION.md)
4. 🚀 **部署上线**: 配置 HTTPS 和生产环境
5. 🔧 **功能扩展**: 添加新功能（屏幕共享、聊天等）

---

## 💡 提示

- **开发模式**: 当前配置适合开发和测试
- **生产部署**: 需要配置 HTTPS、TURN 服务器、域名等
- **性能优化**: 可以调整视频编码参数、分辨率等
- **安全加固**: 添加用户认证、房间密码等

---

**祝您使用愉快！🎊**

有任何问题，请查看相关文档或检查日志输出。
