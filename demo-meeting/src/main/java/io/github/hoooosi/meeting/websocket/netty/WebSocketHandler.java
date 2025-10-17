package io.github.hoooosi.meeting.websocket.netty;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hoooosi.meeting.common.constants.MessageTargetTypes;
import io.github.hoooosi.meeting.common.constants.MessageTypes;
import io.github.hoooosi.meeting.common.constants.TopicNames;
import io.github.hoooosi.meeting.common.model.dto.TokenDTO;
import io.github.hoooosi.meeting.common.model.entity.User;
import io.github.hoooosi.meeting.common.utils.ChannelUtils;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.RocketMQUtils;
import io.github.hoooosi.meeting.common.utils.TokenUtils;
import io.github.hoooosi.meeting.mapper.UserMapper;
import io.github.hoooosi.meeting.websocket.ChannelContextUtils;
import io.github.hoooosi.meeting.websocket.message.MessageSendDTO;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ChannelHandler.Sharable
@Slf4j
@AllArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final ChannelContextUtils channelContextUtils;
    private final UserMapper userMapper;
    private final RocketMQUtils rocketMQUtils;
    private final TokenUtils tokenUtils;
    private final RedisUtils redisUtils;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ChannelUtils.getBindUserId(ctx.channel());
        log.info("Channel inactive: {}, userId: {}", ctx.channel().remoteAddress(), userId);
        TokenDTO tokenDTO = redisUtils.getTokenDTO(userId);
        if (tokenDTO != null && tokenDTO.getRoomId() != null) {
            rocketMQTemplate.convertAndSend(TopicNames.MESSAGE_TOPIC, new MessageSendDTO<>()
                    .setMessageTargetType(MessageTargetTypes.MEETING)
                    .setMessageType(MessageTypes.EVENT)
                    .setReceiverId(tokenDTO.getRoomId().toString())
                    .setContent("USER " + userId + " JOINED MEETING"));
            redisUtils.setRoomId(userId, null);
        }
        userMapper.update(Wrappers
                .lambdaUpdate(User.class)
                .eq(User::getUserId, userId)
                .set(User::getLastOffTime, System.currentTimeMillis()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        try {
            String text = textWebSocketFrame.text();
            log.info("Received: {}", text);
            MessageSendDTO<Object> bean = mapper.readValue(
                    text,
                    new TypeReference<MessageSendDTO<Object>>() {
                    }
            );
            if (MessageTypes.PING == bean.getMessageType())
                return;
            bean.setSenderId(Objects.requireNonNull(ChannelUtils.getBindUserId(ctx.channel())).toString());
            bean.setSendTime(String.valueOf(System.currentTimeMillis()));
            rocketMQTemplate.convertAndSend(TopicNames.MESSAGE_TOPIC, bean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
