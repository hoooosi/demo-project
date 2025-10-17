package io.github.hoooosi.meeting.common.utils;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class ChannelUtils {
    public static Long getBindUserId(Channel channel) {
        AttributeKey<Long> attributeKey = AttributeKey.valueOf(channel.id().toString());
        if (attributeKey == null) return null;
        return channel.attr(attributeKey).get();
    }
}
