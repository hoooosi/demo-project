package io.github.hoooosi.meeting.websocket;

import cn.hutool.json.JSONUtil;
import io.github.hoooosi.meeting.websocket.message.MessageSendDTO;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChannelContextUtils {
    public static final ConcurrentHashMap<Long, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<String, ChannelGroup> MEETING_ROOM_CONTEXT_MAP = new ConcurrentHashMap<>();

    public void addContext(Long userId, Channel channel) {
        try {
            String channelId = channel.id().toString();

            AttributeKey<Channel> key = null;
            if (!AttributeKey.exists(channelId))
                key = AttributeKey.newInstance(channelId);
            else
                key = AttributeKey.valueOf(channelId);
            channel.attr(key).set(channel);

            USER_CONTEXT_MAP.put(userId, channel);
            log.info("User {} connected. Total users: {}", userId, USER_CONTEXT_MAP.size());
        } catch (Exception e) {
            log.error("Error adding user context", e);
        }
    }

    public void addMeetingRoom(String meetingKey, Long userId) {
        Channel context = USER_CONTEXT_MAP.get(userId);

        if (context == null) {
            return;
        }

        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingKey);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            MEETING_ROOM_CONTEXT_MAP.put(meetingKey, group);
        }

        Channel channel = group.find(context.id());
        if (channel == null) {
            group.add(context);
        }
    }

    public void sendMessage(MessageSendDTO<Object> dto) {
        if (dto.getMessageSendType() == 1) {
            this.sendToUser(dto);
        } else if (dto.getMessageSendType() == 2) {
            this.sendToGroup(dto);
        }
    }

    public void sendToUser(MessageSendDTO<Object> dto) {
        if (dto.getMeetingId() == null) {
            return;
        }
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(dto.getMeetingId());
        if (group == null) {
            return;
        }
        group.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));
    }

    public void sendToGroup(MessageSendDTO<Object> dto) {
        if (dto.getMeetingId() == null) {
            return;
        }
        Channel channel = USER_CONTEXT_MAP.get(dto.getReceiverId());
        if (channel == null) {
            return;
        }
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(dto)));
    }

    public void closeContext(Long userId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        USER_CONTEXT_MAP.remove(userId);
        if (channel != null) {
            channel.close();
        }
    }
}
