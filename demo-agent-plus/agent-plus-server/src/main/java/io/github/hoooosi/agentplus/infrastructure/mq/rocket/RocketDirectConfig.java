package io.github.hoooosi.agentplus.infrastructure.mq.rocket;

import io.github.hoooosi.agentplus.infrastructure.mq.core.MessagePublisher;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** RocketMQ configuration using the RocketMQ client. */
@Configuration
@ConditionalOnProperty(prefix = "rocketmq", name = "enabled", havingValue = "true")
public class RocketDirectConfig {

    @Bean
    @ConfigurationProperties(prefix = "rocketmq")
    @Validated
    public RocketMQProperties rocketMQProperties() {
        return new RocketMQProperties();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer rocketMQProducer(RocketMQProperties properties) {
        DefaultMQProducer producer = new DefaultMQProducer(properties.getProducerGroup());
        producer.setNamesrvAddr(properties.getNameServer());

        // Optional configurations
        if (properties.getSendMsgTimeout() != null) {
            producer.setSendMsgTimeout(properties.getSendMsgTimeout());
        }
        if (properties.getRetryTimesWhenSendFailed() != null) {
            producer.setRetryTimesWhenSendFailed(properties.getRetryTimesWhenSendFailed());
        }
        if (properties.getMaxMessageSize() != null) {
            producer.setMaxMessageSize(properties.getMaxMessageSize());
        }

        return producer;
    }

    @Bean
    public MessagePublisher messagePublisher(DefaultMQProducer producer) {
        return new RocketDirectPublisher(producer);
    }

    @Data
    public static class RocketMQProperties {

        @NotBlank(message = "RocketMQ name server address is required")
        private String nameServer;

        @NotBlank(message = "RocketMQ producer group is required")
        private String producerGroup = "default-producer-group";

        /**
         * Send message timeout in milliseconds. Default: 3000
         */
        private Integer sendMsgTimeout;

        /**
         * Retry times when send failed. Default: 2
         */
        private Integer retryTimesWhenSendFailed;

        /**
         * Maximum message size. Default: 4MB
         */
        private Integer maxMessageSize;
    }
}
