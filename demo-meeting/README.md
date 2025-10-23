# Demo Meeting Backend

🎥 一个基于 Spring Boot 3 + Netty + WebRTC 的现代化视频会议系统后端

## Task

- [x] WebSocket 信令服务器
- [x] 会议室管理
- [x] 用户认证与授权
- [ ] 消息队列集成优化
- [ ] 会议录制功能
- [ ] 负载均衡支持

## 🚀 项目简介

Demo Meeting Backend 是一个功能完整的视频会议系统后端服务，提供 WebRTC 信令服务、会议管理、用户管理、实时消息推送等功能。项目采用微服务架构设计，具有高性能、高可用、易扩展的特点。

### 主要特性

- ✅ **WebSocket 信令服务**：基于 Netty 的高性能 WebSocket 服务器
- ✅ **会议管理**：创建会议、加入会议、会议成员管理
- ✅ **用户认证**：JWT Token 认证机制
- ✅ **实时消息**：基于 WebSocket 的实时消息推送
- ✅ **消息队列**：RocketMQ 异步消息处理
- ✅ **对象存储**：MinIO 文件存储服务
- ✅ **API 文档**：Knife4j (Swagger) 自动生成 API 文档
- ✅ **连接管理**：Channel 生命周期管理和自动清理

## 🛠 技术栈

### 核心框架

- **Spring Boot 3.4.5** - 企业级应用开发框架
- **Spring Cloud 2024.0.2** - 微服务生态
- **Spring Cloud Alibaba 2023.0.1.0** - 阿里巴巴微服务组件
- **Netty** - 高性能网络通信框架

### 数据访问

- **MyBatis-Plus 3.5.12** - 增强的 MyBatis 持久层框架
- **PostgreSQL 42.7.2** - 关系型数据库
- **Redis** - 高性能缓存和会话存储

### 消息队列

- **Apache RocketMQ 2.3.0** - 分布式消息中间件

### 对象存储

- **MinIO** - 开源对象存储服务

### 工具类库

- **Hutool 5.8.26** - Java 工具类库
- **Guava 32.0.1** - Google 核心 Java 库
- **Lombok** - 简化 Java 代码

### API 文档

- **Knife4j 4.4.0** - Swagger 增强 UI
- **SpringDoc 2.8.13** - OpenAPI 3.0 规范

## 🏗 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                          │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────────┐ │
│  │  Web App   │  │  Mobile App │  │   Desktop App    │ │
│  └────────────┘  └─────────────┘  └──────────────────┘ │
└────────────┬────────────────────────────────┬───────────┘
             │                                │
             ▼                                ▼
┌────────────────────────┐      ┌────────────────────────┐
│   WebSocket (Netty)    │      │    HTTP REST API       │
│  ┌──────────────────┐  │      │  ┌──────────────────┐  │
│  │ Token Validation │  │      │  │  Authentication  │  │
│  │ Heart Beat       │  │      │  │  Authorization   │  │
│  │ Message Handler  │  │      │  │  CORS Config     │  │
│  └──────────────────┘  │      │  └──────────────────┘  │
└────────────┬───────────┘      └────────────┬───────────┘
             │                                │
             ▼                                ▼
┌─────────────────────────────────────────────────────────┐
│                    Service Layer                         │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────────┐ │
│  │   Meeting    │  │    User     │  │     Message    │ │
│  │   Service    │  │   Service   │  │     Service    │ │
│  └──────────────┘  └─────────────┘  └────────────────┘ │
└────────────┬────────────────────────────────┬───────────┘
             │                                │
             ▼                                ▼
┌─────────────────────────────────────────────────────────┐
│                 Data Access Layer                        │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────────┐ │
│  │  PostgreSQL  │  │    Redis    │  │     MinIO      │ │
│  │  (MyBatis+)  │  │   (Cache)   │  │  (Storage)     │ │
│  └──────────────┘  └─────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│                Message Queue Layer                       │
│  ┌──────────────────────────────────────────────────┐   │
│  │              RocketMQ                            │   │
│  │  ┌──────────────┐  ┌──────────────────────────┐ │   │
│  │  │  NameServer  │  │       Broker             │ │   │
│  │  └──────────────┘  └──────────────────────────┘ │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### WebSocket 消息流程

```
Client                  Netty Server              Service Layer
  │                           │                         │
  │──① WebSocket Connect─────>│                         │
  │                           │──② Token Validation────>│
  │                           │<──③ Validation Result───│
  │<──④ Connection Success────│                         │
  │                           │──⑤ Register Channel────>│
  │                           │                         │
  │──⑥ Join Meeting Request──>│                         │
  │                           │──⑦ Business Logic──────>│
  │                           │<──⑧ Add to Room─────────│
  │                           │                         │
  │──⑨ Send Message──────────>│                         │
  │                           │──⑩ Route Message───────>│
  │<──⑪ Broadcast Message─────│<──⑫ Target Users───────│
  │                           │                         │
  │──⑬ Heartbeat Ping────────>│                         │
  │<──⑭ Heartbeat Pong────────│                         │
  │                           │                         │
  │──⑮ Disconnect────────────>│                         │
  │                           │──⑯ Cleanup Resources───>│
```

### Channel 生命周期管理

```
┌─────────────────────────────────────────────────────────┐
│                Channel Lifecycle                         │
│                                                          │
│  ┌──────────┐      ┌──────────┐      ┌──────────────┐  │
│  │ Connect  │─────>│  Active  │─────>│   Inactive   │  │
│  └──────────┘      └──────────┘      └──────────────┘  │
│       │                  │                    │         │
│       ▼                  ▼                    ▼         │
│  ┌──────────────────────────────────────────────────┐  │
│  │          ChannelContextUtils                     │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │  USER_CONTEXT_MAP (userId -> Channel)     │  │  │
│  │  │  - addContext()                           │  │  │
│  │  │  - closeContext()                         │  │  │
│  │  └────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │  MEETING_ROOM_CONTEXT_MAP                 │  │  │
│  │  │  (meetingNo -> ChannelGroup)              │  │  │
│  │  │  - addMeetingRoom()                       │  │  │
│  │  │  - removeFromMeetingRoom()                │  │  │
│  │  │  - removeChannelFromAllMeetings()         │  │  │
│  │  └────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## 📁 目录结构

```
demo-meeting/
├── !docker/                     # Docker 相关配置和数据
│   ├── config/                 # 服务配置文件
│   │   ├── broker.conf        # RocketMQ Broker 配置
│   │   └── redis.conf         # Redis 配置
│   └── data/                   # 持久化数据目录
│       ├── broker/            # RocketMQ 数据
│       ├── minio/             # MinIO 数据
│       ├── postgres/          # PostgreSQL 数据
│       └── redis/             # Redis 数据
│
├── docs/                        # 文档目录
│   └── sql/                    # SQL 脚本
│       └── table.sql          # 数据库表结构
│
├── src/
│   └── main/
│       ├── java/
│       │   └── io/github/hoooosi/meeting/
│       │       ├── App.java                    # 应用启动类
│       │       │
│       │       ├── aspect/                     # AOP 切面
│       │       │   └── AuthInterceptor.java   # 认证拦截器
│       │       │
│       │       ├── common/                     # 公共模块
│       │       │   ├── constants/             # 常量定义
│       │       │   │   ├── MessageTypes.java         # 消息类型
│       │       │   │   └── MessageTargetTypes.java   # 消息目标类型
│       │       │   ├── exception/             # 异常处理
│       │       │   │   ├── ErrorCode.java            # 错误码
│       │       │   │   ├── BusinessException.java    # 业务异常
│       │       │   │   └── GlobalExceptionHandler.java
│       │       │   ├── model/                 # 数据模型
│       │       │   │   ├── dto/              # 数据传输对象
│       │       │   │   ├── entity/           # 实体类
│       │       │   │   └── vo/               # 视图对象
│       │       │   └── utils/                 # 工具类
│       │       │       ├── ChannelUtils.java         # Channel 工具
│       │       │       ├── RedisUtils.java           # Redis 工具
│       │       │       ├── RocketMQUtils.java        # RocketMQ 工具
│       │       │       ├── TokenUtils.java           # Token 工具
│       │       │       └── ThrowUtils.java           # 异常抛出工具
│       │       │
│       │       ├── config/                     # 配置类
│       │       │   ├── CorsConfig.java        # 跨域配置
│       │       │   ├── MinioConfig.java       # MinIO 配置
│       │       │   └── RedisConfig.java       # Redis 配置
│       │       │
│       │       ├── controller/                 # 控制器层
│       │       │   ├── UserController.java    # 用户接口
│       │       │   ├── MeetingController.java # 会议接口
│       │       │   └── MessageController.java # 消息接口
│       │       │
│       │       ├── mapper/                     # 数据访问层
│       │       │   ├── UserMapper.java
│       │       │   ├── MeetingMapper.java
│       │       │   └── MeetingMemberMapper.java
│       │       │
│       │       ├── service/                    # 服务层
│       │       │   ├── UserService.java
│       │       │   ├── MeetingService.java
│       │       │   ├── MeetingMemberService.java
│       │       │   └── impl/                  # 服务实现
│       │       │       ├── UserServiceImpl.java
│       │       │       ├── MeetingServiceImpl.java
│       │       │       └── MeetingMemberServiceImpl.java
│       │       │
│       │       └── websocket/                  # WebSocket 模块
│       │           ├── ChannelContextUtils.java       # Channel 上下文管理
│       │           ├── message/               # 消息定义
│       │           │   └── MessageSendDTO.java
│       │           └── netty/                 # Netty 处理器
│       │               ├── NettyWebSocketStarter.java    # Netty 启动器
│       │               ├── WebSocketHandler.java         # WebSocket 处理器
│       │               ├── TokenValidationHandler.java   # Token 验证
│       │               └── HeartHandler.java             # 心跳处理
│       │
│       └── resources/
│           └── application.yml         # 应用配置文件
│
├── docker-compose.yml              # Docker Compose 配置
├── pom.xml                         # Maven 项目配置
└── README.md                       # 项目说明
```

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **Maven 3.6+**
- **Docker & Docker Compose** (推荐)

### 使用 Docker Compose 启动

项目提供了完整的 Docker Compose 配置，可一键启动所有依赖服务：

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

### 服务端口

Docker Compose 启动后，以下服务将可用：

| 服务                | 端口                | 说明                |
| ------------------- | ------------------- | ------------------- |
| PostgreSQL          | 5432                | 关系型数据库        |
| Redis               | 6379                | 缓存服务            |
| MinIO               | 9000                | 对象存储 API        |
| MinIO Console       | 9001                | MinIO 管理控制台    |
| RocketMQ NameServer | 9876                | 消息队列命名服务    |
| RocketMQ Broker     | 10909, 10911, 10912 | 消息队列代理服务    |
| RocketMQ Dashboard  | 8180                | RocketMQ 管理控制台 |

### 初始化数据库

```bash
# 连接到 PostgreSQL
psql -h localhost -p 5432 -U postgres

# 创建数据库
CREATE DATABASE meeting;

# 执行初始化脚本
\c meeting
\i docs/sql/table.sql
```

### 配置文件

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 80

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/meeting
    username: postgres
    password: 123456

  data:
    redis:
      host: localhost
      port: 6379
      password: redis
      timeout: 5000

rocketmq:
  name-server: localhost:9876
  producer:
    group: meeting-producer-group
    send-message-timeout: 3000
    retry-times-when-send-failed: 2

minio:
  endpoint: http://localhost:9000
  access-key: minio
  secret-key: 12345678
  bucket-name: picture
```

### 编译和运行

```bash
# 使用 Maven 编译
mvn clean package -DskipTests

# 运行应用
java -jar target/meeting-1.0-SNAPSHOT.jar

# 或使用 Maven 直接运行
mvn spring-boot:run
```

应用将在 `http://localhost:80` 启动

### 访问 API 文档

启动应用后，访问 Knife4j API 文档：

```
http://localhost:80/doc.html
```
