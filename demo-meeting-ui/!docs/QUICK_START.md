# 🚀 快速开始指南

## 一键启动（推荐）

### Windows 用户
```powershell
.\start.ps1
```

### Linux/Mac 用户
```bash
chmod +x start.sh
./start.sh
```

## 手动启动

### 1️⃣ 启动后端

```bash
cd demo-meeting
mvn clean package -DskipTests
java -jar target/meeting-1.0-SNAPSHOT.jar
```

### 2️⃣ 启动前端

**新开一个终端窗口**

```bash
cd demo-meeting-ui
bun install  # 或 npm install
bun run dev  # 或 npm run dev
```

## 📝 使用步骤

### 1. 访问主页
打开浏览器访问：http://localhost:5173

### 2. 创建/加入房间
- 输入你的用户名（例如：张三）
- 输入房间ID（例如：room-123），或点击"创建随机房间"
- 点击"加入房间"

### 3. 授权设备
- 浏览器会请求访问摄像头和麦克风
- 点击"允许"授权

### 4. 邀请其他人
- 将房间ID分享给其他人
- 他们使用相同的房间ID加入即可开始视频通话

## 🧪 测试信令服务器

访问测试页面：http://localhost:5173/test-signaling.html

这是一个独立的测试工具，可以：
- 测试 WebSocket 连接
- 发送各种信令消息
- 查看服务器响应
- 调试问题

## 🔍 常见问题

### Q: 看不到对方的视频？
A: 
1. 检查网络连接
2. 确保双方都授权了摄像头和麦克风
3. 查看浏览器控制台是否有错误
4. 尝试刷新页面重新加入

### Q: 提示 "无法访问摄像头或麦克风"？
A:
1. 检查设备是否被其他应用占用
2. 在浏览器设置中允许该网站访问设备
3. 确保使用 localhost 或 HTTPS

### Q: 连接信令服务器失败？
A:
1. 确认后端服务已启动
2. 检查 8090 端口是否被占用
3. 查看后端日志输出

### Q: 多人会议时卡顿？
A:
1. 检查网络带宽
2. 关闭不必要的视频流
3. 考虑配置 TURN 服务器

## 📱 支持的浏览器

- ✅ Chrome 74+
- ✅ Firefox 66+
- ✅ Safari 12.1+
- ✅ Edge 79+
- ❌ IE（不支持 WebRTC）

## 🌐 网络要求

- **本地测试**: 无需特殊配置
- **局域网**: 确保防火墙允许相关端口
- **公网**: 需要配置 TURN 服务器（复杂 NAT 环境）

## 📊 端口说明

| 端口 | 服务 | 说明 |
|------|------|------|
| 8080 | REST API | Spring Boot HTTP 服务 |
| 8089 | WebSocket | 通用 WebSocket 服务 |
| 8090 | WebRTC 信令 | WebRTC 专用信令服务器 |
| 5173 | 前端开发服务器 | Vite 开发服务器 |

## 💡 提示

1. **首次使用**: 建议先打开测试页面验证信令服务器工作正常
2. **多人测试**: 可以在不同浏览器标签页或设备上打开
3. **开发调试**: 打开浏览器开发者工具查看详细日志
4. **生产部署**: 建议配置 HTTPS 和专业的 TURN 服务器

## 🎯 下一步

- 阅读完整文档：[WEBRTC_README.md](./WEBRTC_README.md)
- 查看 API 文档：http://localhost:8080/doc.html
- 自定义配置：修改 `application.yml` 和前端配置文件

---

**Enjoy! 🎉**
