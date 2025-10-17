package io.github.hoooosi.meeting.common.utils;

import io.github.hoooosi.meeting.common.constants.TopicNames;
import io.github.hoooosi.meeting.websocket.message.MessageSendDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RocketMQUtils {
    private final RocketMQTemplate rocketMQTemplate;

    public void sendMessage(MessageSendDTO<Object> dto) {
        log.info("Sending message: {}", dto);
        rocketMQTemplate.convertAndSend(TopicNames.MESSAGE_TOPIC, dto);
    }
}
