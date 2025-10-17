package io.github.hoooosi.meeting.websocket;

import cn.hutool.json.JSONUtil;
import io.github.hoooosi.meeting.common.constants.MessageTargetTypes;
import io.github.hoooosi.meeting.common.constants.MessageTypes;
import io.github.hoooosi.meeting.common.constants.TopicNames;
import io.github.hoooosi.meeting.common.exception.BusinessException;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.common.utils.ChannelUtils;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import io.github.hoooosi.meeting.websocket.message.MessageSendDTO;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChannelContextUtils {
    public static final ConcurrentHashMap<Long, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Long, ChannelGroup> MEETING_ROOM_CONTEXT_MAP = new ConcurrentHashMap<>();
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisUtils redisUtils;

    public ChannelContextUtils(RocketMQTemplate rocketMQTemplate, RedisUtils redisUtils) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.redisUtils = redisUtils;
    }

    public void addContext(Long userId, Channel channel) {
        try {
            String channelId = channel.id().toString();

            AttributeKey<Long> key = null;
            if (!AttributeKey.exists(channelId))
                key = AttributeKey.newInstance(channelId);
            else
                key = AttributeKey.valueOf(channelId);
            channel.attr(key).set(userId);

            USER_CONTEXT_MAP.put(userId, channel);
            log.info("User {} connected. Total users: {}", userId, USER_CONTEXT_MAP.size());
        } catch (Exception e) {
            log.error("Error adding user context", e);
        }
    }

    public void addMeetingRoom(Long meetingNo, Long userId) {
        Channel context = USER_CONTEXT_MAP.get(userId);
        ThrowUtils.throwIfNull(context, ErrorCode.CONNECTION_ERROR);

        if (!context.isActive())
            throw new BusinessException(ErrorCode.CONNECTION_ERROR);

        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingNo);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            MEETING_ROOM_CONTEXT_MAP.put(meetingNo, group);
        }

        Channel channel = group.find(context.id());
        if (channel == null)
            group.add(context);

        redisUtils.setRoomId(userId, meetingNo);

        rocketMQTemplate.convertAndSend(TopicNames.MESSAGE_TOPIC, new MessageSendDTO<>()
                .setMessageTargetType(MessageTargetTypes.MEETING)
                .setMessageType(MessageTypes.EVENT)
                .setReceiverId(meetingNo.toString())
                .setContent("USER " + userId + " JOINED MEETING"));
    }

    public void leaveRoom(Long meetingNo, Long userId) {
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingNo);
        if (group == null)
            return;
        group.remove(USER_CONTEXT_MAP.get(userId));
        redisUtils.setRoomId(userId, null);
        rocketMQTemplate.convertAndSend(TopicNames.MESSAGE_TOPIC, new MessageSendDTO<>()
                .setMessageTargetType(MessageTargetTypes.MEETING)
                .setMessageType(MessageTypes.EVENT)
                .setReceiverId(meetingNo.toString())
                .setContent("USER " + userId + " LEFT MEETING"));
    }

    public List<Long> getMeetingRoomUserIds(Long meetingNo) {
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingNo);
        if (group == null) {
            return List.of();
        }
        return group.stream()
                .map(ChannelUtils::getBindUserId)
                .toList();
    }

    public void sendMessage(MessageSendDTO<Object> dto) {
        switch (dto.getMessageTargetType()) {
            case MessageTargetTypes.PRIVATE -> this.sendToUser(dto);
            case MessageTargetTypes.MEETING -> this.sendToMeeting(dto);
        }
    }

    public void sendToUser(MessageSendDTO<Object> dto) {
        if (dto.getReceiverId() == null) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.get(Long.parseLong(dto.getReceiverId()));
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));
    }

    public void sendToMeeting(MessageSendDTO<Object> dto) {
        if (dto.getReceiverId() == null) {
            return;
        }
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(Long.parseLong(dto.getReceiverId()));
        if (group == null) {
            return;
        }
        group.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));
    }

    public void closeContext(Long userId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        USER_CONTEXT_MAP.remove(userId);
        if (channel != null) {
            MEETING_ROOM_CONTEXT_MAP.forEach((meetingNo, group) -> {
                if (group.remove(channel)) {
                    if (group.isEmpty())
                        MEETING_ROOM_CONTEXT_MAP.remove(meetingNo);
                }
            });
            channel.close();
        }
    }
}
