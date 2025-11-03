package io.github.hoooosi.agentplus.infrastructure.mq.rocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessageEnvelope;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessageHeaders;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessagePublisher;
import io.github.hoooosi.agentplus.infrastructure.mq.core.MessageRoute;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/** Direct RocketMQ publisher using the RocketMQ client API. */
public final class RocketDirectPublisher implements MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(RocketDirectPublisher.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final DefaultMQProducer producer;

    public RocketDirectPublisher(DefaultMQProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(MessageRoute route, MessageEnvelope<?> envelope) {
        publish(route, envelope, null);
    }

    @Override
    public void publish(MessageRoute route, MessageEnvelope<?> envelope, Long ttlMillis) {
        try {
            String body = MAPPER.writeValueAsString(envelope);

            // Create RocketMQ message
            // In RocketMQ: topic = exchange, tags = routingKey
            Message message = new Message(
                    route.getExchange(), // topic
                    route.getRoutingKey(), // tags
                    body.getBytes(StandardCharsets.UTF_8));

            // Set trace ID as user property
            if (envelope.getTraceId() != null) {
                message.putUserProperty(MessageHeaders.TRACE_ID, envelope.getTraceId());
            }

            // Set message delay level if TTL is specified
            // RocketMQ doesn't support arbitrary TTL, but supports delay levels
            if (ttlMillis != null && ttlMillis > 0) {
                // This is a simplified mapping, adjust as needed
                int delayLevel = calculateDelayLevel(ttlMillis);
                if (delayLevel > 0) {
                    message.setDelayTimeLevel(delayLevel);
                }
            }

            // Send message
            SendResult sendResult = producer.send(message);

            log.debug("Published message to topic:{}, tags:{}, msgId:{}, status:{}",
                    route.getExchange(),
                    route.getRoutingKey(),
                    sendResult.getMsgId(),
                    sendResult.getSendStatus());

        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish message to RocketMQ", e);
        }
    }

    /** Calculate RocketMQ delay level from TTL milliseconds.
     * RocketMQ delay levels: 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m
     * 1h 2h
     * @param ttlMillis TTL in milliseconds
     * @return delay level (0 means no delay) */
    private int calculateDelayLevel(long ttlMillis) {
        long seconds = ttlMillis / 1000;

        if (seconds < 1)
            return 0;
        if (seconds <= 1)
            return 1; // 1s
        if (seconds <= 5)
            return 2; // 5s
        if (seconds <= 10)
            return 3; // 10s
        if (seconds <= 30)
            return 4; // 30s
        if (seconds <= 60)
            return 5; // 1m
        if (seconds <= 120)
            return 6; // 2m
        if (seconds <= 180)
            return 7; // 3m
        if (seconds <= 240)
            return 8; // 4m
        if (seconds <= 300)
            return 9; // 5m
        if (seconds <= 360)
            return 10; // 6m
        if (seconds <= 420)
            return 11; // 7m
        if (seconds <= 480)
            return 12; // 8m
        if (seconds <= 540)
            return 13; // 9m
        if (seconds <= 600)
            return 14; // 10m
        if (seconds <= 1200)
            return 15; // 20m
        if (seconds <= 1800)
            return 16; // 30m
        if (seconds <= 3600)
            return 17; // 1h
        return 18; // 2h
    }
}
