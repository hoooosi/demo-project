package io.github.hoooosi.demo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 * 
 * 原理说明：
 * 1. 继承 SimpleChannelInboundHandler 处理 WebSocket 帧
 * 2. 使用 ChannelGroup 管理所有连接的客户端
 * 3. 支持广播消息到所有在线客户端
 * 4. 自动处理连接建立、断开、异常等生命周期事件
 */
@Slf4j
@Component
public class AgentWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    // 全局 Channel 组，存储所有活跃连接
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // Channel 与用户 ID 的映射
    private final Map<String, String> channelUserMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 连接建立时调用
     * 
     * 原理：当 WebSocket 握手完成后，将 Channel 添加到全局组中
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asShortText();
        channelGroup.add(ctx.channel());
        log.info("WebSocket 连接建立: channelId={}, 当前连接数: {}",
                channelId, channelGroup.size());

        // 发送欢迎消息
        sendMessage(ctx, Map.of(
                "type", "CONNECTED",
                "message", "WebSocket 连接成功",
                "channelId", channelId,
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 连接断开时调用
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asShortText();
        channelGroup.remove(ctx.channel());
        channelUserMap.remove(channelId);
        log.info("WebSocket 连接断开: channelId={}, 剩余连接数: {}",
                channelId, channelGroup.size());
    }

    /**
     * 接收客户端消息
     * 
     * 原理：
     * 1. 读取 WebSocket 文本帧
     * 2. 解析 JSON 消息
     * 3. 根据消息类型分发处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 目前只处理文本帧
        if (frame instanceof TextWebSocketFrame textFrame) {
            String message = textFrame.text();
            String channelId = ctx.channel().id().asShortText();

            log.info("收到客户端消息: channelId={}, message={}", channelId, message);

            try {
                // 解析消息
                @SuppressWarnings("unchecked")
                Map<String, Object> msgMap = objectMapper.readValue(message, Map.class);
                String msgType = (String) msgMap.get("type");

                // 根据消息类型处理
                switch (msgType) {
                    case "PING":
                        // 心跳检测
                        sendMessage(ctx, Map.of("type", "PONG", "timestamp", System.currentTimeMillis()));
                        break;
                    case "REGISTER":
                        // 注册用户 ID
                        String userId = (String) msgMap.get("userId");
                        channelUserMap.put(channelId, userId);
                        log.info("用户注册成功: userId={}, channelId={}", userId, channelId);
                        break;
                    default:
                        log.warn("未知消息类型: {}", msgType);
                }

            } catch (Exception e) {
                log.error("解析客户端消息失败", e);
                sendMessage(ctx, Map.of(
                        "type", "ERROR",
                        "message", "消息格式错误: " + e.getMessage()));
            }
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        String channelId = ctx.channel().id().asShortText();
        log.error("WebSocket 异常: channelId={}", channelId, cause);
        ctx.close();
    }

    /**
     * 发送消息到指定客户端
     */
    private void sendMessage(ChannelHandlerContext ctx, Map<String, Object> message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(json));
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    /**
     * 广播消息到所有客户端
     * 
     * 原理：遍历 ChannelGroup 中的所有 Channel，逐个发送消息
     */
    public void broadcast(Map<String, Object> message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            TextWebSocketFrame frame = new TextWebSocketFrame(json);
            channelGroup.writeAndFlush(frame);
            log.debug("广播消息到 {} 个客户端", channelGroup.size());
        } catch (Exception e) {
            log.error("广播消息失败", e);
        }
    }

    /**
     * 发送虚拟机操作更新（供外部调用）
     */
    public void sendVmUpdate(String type, String message, Object data) {
        broadcast(Map.of(
                "type", type,
                "message", message,
                "data", data != null ? data : "",
                "timestamp", LocalDateTime.now().toString()));
    }

    /**
     * 获取当前连接数
     */
    public int getConnectionCount() {
        return channelGroup.size();
    }
}
