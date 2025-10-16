# ⚙️ 配置说明

## 后端配置

### application.yml

位置: `demo-meeting/src/main/resources/application.yml`

```yaml
server:
  port: 8080  # REST API 端口

# WebRTC 信令服务器端口在代码中配置
# 位置: WebRTCSignalingServer.java
# 默认端口: 8090
```

### 修改信令服务器端口

编辑 `demo-meeting/src/main/java/io/github/hoooosi/meeting/websocket/webrtc/WebRTCSignalingServer.java`:

```java
private static final int WEBRTC_PORT = 8090;  // 修改这里
```

### CORS 配置

CORS 已经在 `CorsConfig.java` 中配置为允许所有来源，如需限制请修改：

```java
// 允许特定域名
config.addAllowedOrigin("http://localhost:5173");
config.addAllowedOrigin("http://example.com");

// 或使用模式匹配
config.addAllowedOriginPattern("http://*.example.com");
```

## 前端配置

### 修改信令服务器地址

编辑 `demo-meeting-ui/src/views/VideoCall.vue`:

```javascript
// 第 182 行左右
const wsUrl = `ws://localhost:8090/webrtc`;  // 修改服务器地址
```

### 修改 STUN/TURN 服务器

编辑 `demo-meeting-ui/src/utils/webrtc-manager.js`:

```javascript
this.configuration = {
  iceServers: [
    // 公共 STUN 服务器
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' },
    
    // 自建 TURN 服务器（可选）
    { 
      urls: 'turn:your-turn-server.com:3478',
      username: 'your-username',
      credential: 'your-password'
    }
  ]
};
```

### Vite 开发服务器配置

编辑 `demo-meeting-ui/vite.config.ts`:

```typescript
export default defineConfig({
  server: {
    port: 5173,  // 修改端口
    host: '0.0.0.0',  // 允许外部访问
    proxy: {
      // 如果需要代理 API 请求
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## TURN 服务器配置

### 为什么需要 TURN？

在以下情况下，P2P 连接可能失败，需要 TURN 服务器中转：
- 严格的 NAT 环境
- 对称型 NAT
- 企业防火墙
- 某些移动网络

### 安装 Coturn (推荐的 TURN 服务器)

#### Ubuntu/Debian
```bash
sudo apt-get install coturn
```

#### Docker
```bash
docker run -d --network=host \
  -e DETECT_EXTERNAL_IP=yes \
  -e DETECT_RELAY_IP=yes \
  -e USERNAME=your-username \
  -e PASSWORD=your-password \
  coturn/coturn
```

#### 配置文件 `/etc/turnserver.conf`
```conf
# 监听端口
listening-port=3478
tls-listening-port=5349

# 外部 IP (公网 IP)
external-ip=YOUR_PUBLIC_IP

# Realm
realm=your-domain.com

# 用户认证
user=username:password

# 日志
log-file=/var/log/turnserver.log
verbose
```

#### 启动服务
```bash
sudo systemctl start coturn
sudo systemctl enable coturn
```

### 在前端使用 TURN 服务器

```javascript
this.configuration = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { 
      urls: 'turn:your-server.com:3478',
      username: 'username',
      credential: 'password'
    },
    {
      urls: 'turns:your-server.com:5349',  // TLS
      username: 'username',
      credential: 'password'
    }
  ]
};
```

## 生产环境配置建议

### 1. 使用 HTTPS/WSS

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    # 前端
    location / {
        root /path/to/demo-meeting-ui/dist;
        try_files $uri $uri/ /index.html;
    }

    # REST API
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # WebRTC 信令 WebSocket
    location /webrtc {
        proxy_pass http://localhost:8090/webrtc;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_read_timeout 86400;
    }
}
```

### 2. 修改前端 WebSocket 地址

```javascript
// 开发环境
const wsUrl = `ws://localhost:8090/webrtc`;

// 生产环境
const wsUrl = `wss://${window.location.host}/webrtc`;
```

### 3. 配置环境变量

创建 `.env.production`:

```env
VITE_WS_URL=wss://your-domain.com/webrtc
VITE_API_URL=https://your-domain.com/api
```

使用环境变量:

```javascript
const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8090/webrtc';
```

### 4. 构建生产版本

```bash
cd demo-meeting-ui
bun run build  # 或 npm run build

# 输出目录: dist/
```

### 5. 后端打包

```bash
cd demo-meeting
mvn clean package -DskipTests

# 输出: target/meeting-1.0-SNAPSHOT.jar
```

## 性能优化

### 后端优化

```yaml
# application.yml
server:
  tomcat:
    threads:
      max: 200
      min-spare: 10
    max-connections: 10000
```

### Netty 优化

```java
// WebRTCSignalingServer.java
private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
private final EventLoopGroup workerGroup = new NioEventLoopGroup(8);  // 增加工作线程
```

### 前端优化

1. **启用视频编码优化**
```javascript
const offer = await pc.createOffer({
  offerToReceiveAudio: true,
  offerToReceiveVideo: true,
  voiceActivityDetection: true
});
```

2. **设置视频质量**
```javascript
const stream = await navigator.mediaDevices.getUserMedia({
  video: {
    width: { ideal: 1280, max: 1920 },
    height: { ideal: 720, max: 1080 },
    frameRate: { ideal: 24, max: 30 }
  },
  audio: true
});
```

## 监控和日志

### 后端日志

```yaml
# application.yml
logging:
  level:
    io.github.hoooosi.meeting: DEBUG
    io.netty: INFO
  file:
    name: logs/application.log
```

### 前端日志

在浏览器控制台查看：
- WebSocket 连接状态
- WebRTC 连接状态
- ICE 候选信息
- 媒体流信息

## 故障排查

### 启用详细日志

**后端:**
```yaml
logging:
  level:
    io.netty.handler.codec.http: DEBUG
    io.netty.handler.codec.http.websocketx: DEBUG
```

**前端:**
```javascript
// webrtc-manager.js
pc.oniceconnectionstatechange = () => {
  console.log(`ICE state: ${pc.iceConnectionState}`);
  console.log(`Connection state: ${pc.connectionState}`);
  console.log(`Signaling state: ${pc.signalingState}`);
};
```

---

需要更多帮助？查看 [WEBRTC_README.md](./WEBRTC_README.md) 或 [QUICK_START.md](./QUICK_START.md)
