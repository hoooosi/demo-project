package io.github.hoooosi.meeting.common.rocketMQ;

import io.github.hoooosi.meeting.common.constants.TopicNames;
import io.github.hoooosi.meeting.websocket.ChannelContextUtils;
import io.github.hoooosi.meeting.websocket.message.MessageSendDTO;
import lombok.AllArgsConstructor;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@RocketMQMessageListener(topic = TopicNames.MESSAGE_TOPIC, consumerGroup = "consumer-group", messageModel = MessageModel.BROADCASTING)
public class MessageListener implements RocketMQListener<MessageSendDTO<Object>> {
    private final ChannelContextUtils channelContextUtils;

    @Override
    public void onMessage(MessageSendDTO<Object> dto) {
        channelContextUtils.sendMessage(dto);
    }
}
