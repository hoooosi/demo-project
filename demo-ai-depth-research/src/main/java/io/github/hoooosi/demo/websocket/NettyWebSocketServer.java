package io.github.hoooosi.demo.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 服务器
 * 
 * 原理说明：
 * 1. 使用 Netty 构建高性能 WebSocket 服务器
 * 2. 采用主从 Reactor 模式（Boss + Worker 线程组）
 * 3. 自定义管道（Pipeline）处理 HTTP 升级和 WebSocket 帧
 * 4. 支持实时双向通信，将虚拟机操作推送到前端
 * 
 * Netty 优势：
 * - 高并发：基于 NIO 的事件驱动架构
 * - 低延迟：零拷贝、直接内存等优化技术
 * - 灵活性：完全掌控通信协议栈
 */
@Slf4j
@Component
public class NettyWebSocketServer {

    @Value("${websocket.port:8081}")
    private int port;

    @Value("${websocket.path:/ws/agent}")
    private String path;

    private EventLoopGroup bossGroup; // 接收连接的线程组
    private EventLoopGroup workerGroup; // 处理 I/O 的线程组
    private Channel serverChannel;

    private final AgentWebSocketHandler webSocketHandler;

    public NettyWebSocketServer(AgentWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * 启动 WebSocket 服务器
     * 
     * 原理：
     * 1. Boss Group：负责接收客户端连接请求
     * 2. Worker Group：负责处理已连接的 Socket 的 I/O 操作
     * 3. ChannelPipeline：责任链模式处理消息
     */
    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                // 创建线程组
                bossGroup = new NioEventLoopGroup(1); // 1 个线程接收连接
                workerGroup = new NioEventLoopGroup(); // CPU 核心数 * 2 个线程处理 I/O

                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class) // 使用 NIO 模式
                        .option(ChannelOption.SO_BACKLOG, 128) // 连接队列大小
                        .handler(new LoggingHandler(LogLevel.INFO)) // Boss Group 日志
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();

                                // HTTP 编解码器（WebSocket 基于 HTTP 协议升级）
                                pipeline.addLast(new HttpServerCodec());

                                // HTTP 消息聚合器（将多个 HTTP 消息聚合为一个完整的 FullHttpRequest）
                                pipeline.addLast(new HttpObjectAggregator(65536));

                                // 支持大文件传输（分块写）
                                pipeline.addLast(new ChunkedWriteHandler());

                                // WebSocket 协议处理器（自动处理握手、ping/pong、close）
                                pipeline.addLast(new WebSocketServerProtocolHandler(
                                        path, // WebSocket 路径
                                        null, // 子协议
                                        true, // 允许扩展
                                        65536, // 最大帧大小
                                        false, // 不允许 mask
                                        true, // 检查 UTF-8
                                        10000L // 握手超时时间（毫秒）
                                ));

                                // 自定义业务处理器
                                pipeline.addLast(webSocketHandler);
                            }
                        });

                // 绑定端口并启动服务器
                ChannelFuture future = bootstrap.bind(port).sync();
                serverChannel = future.channel();

                log.info("Netty WebSocket 服务器启动成功，监听端口: {}, 路径: {}", port, path);
                log.info("WebSocket 连接地址: ws://localhost:{}{}", port, path);

                // 等待服务器 Socket 关闭
                serverChannel.closeFuture().sync();

            } catch (InterruptedException e) {
                log.error("WebSocket 服务器启动失败", e);
                Thread.currentThread().interrupt();
            } finally {
                shutdown();
            }
        }, "netty-websocket-server").start();
    }

    /**
     * 关闭 WebSocket 服务器
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭 WebSocket 服务器...");

        if (serverChannel != null) {
            serverChannel.close();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.info("WebSocket 服务器已关闭");
    }
}
