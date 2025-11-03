package io.github.hoooosi.agentplus.infrastructure.mq.core;

/** Common header keys for messaging. */
public final class MessageHeaders {

    private MessageHeaders() {
    }

    /** Trace id header used across the system. */
    public static final String TRACE_ID = "seqId";
}
