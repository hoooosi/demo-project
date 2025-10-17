package io.github.hoooosi.meeting.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class NettyWebSocketStarter implements ApplicationRunner {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final TokenValidationHandler tokenValidationHandler;
    private final WebSocketHandler webSocketHandler;

    @PreDestroy
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(65536))
//                                    .addLast(new IdleStateHandler(60, 0, 0))
//                                    .addLast(new HeartHandler())
                                    .addLast(tokenValidationHandler)
                                    .addLast(webSocketHandler)
                                    .addLast(new WebSocketServerProtocolHandler("/ws", null, true, 65536, true, true));
                        }
                    });
            Channel channel = bootstrap.bind(8089).sync().channel();
            log.info("WebSocket started");
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("Netty Error", e);
        } finally {
            this.shutdown();
        }
    }
}
