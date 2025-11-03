package io.github.hoooosi.agentplus.infrastructure.mq.core;

import lombok.Getter;

import java.util.Objects;

/** A lightweight route descriptor describing where to publish a message. */
@Getter
public final class MessageRoute {

    private final String exchange;
    private final String routingKey;
    private final String queue;
    private final String type; // e.g. topic, fanout, direct

    private MessageRoute(String exchange, String routingKey, String queue, String type) {
        this.exchange = Objects.requireNonNull(exchange, "exchange");
        this.routingKey = Objects.requireNonNull(routingKey, "routingKey");
        this.queue = Objects.requireNonNull(queue, "queue");
        this.type = Objects.requireNonNull(type, "type");
    }

    public static MessageRoute topic(String exchange, String routingKey, String queue) {
        return new MessageRoute(exchange, routingKey, queue, "topic");
    }
}
