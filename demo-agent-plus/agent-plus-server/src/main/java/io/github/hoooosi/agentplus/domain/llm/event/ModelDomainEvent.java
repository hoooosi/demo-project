package io.github.hoooosi.agentplus.domain.llm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 模型领域事件基类 */
@Getter
public abstract class ModelDomainEvent {
    /** 模型ID */
    private final Long modelId;

    /** 用户ID */
    private final Long userId;

    /** 事件发生时间 */
    private final Long occurredAt;

    protected ModelDomainEvent(Long modelId, Long userId) {
        this.modelId = modelId;
        this.userId = userId;
        this.occurredAt = System.currentTimeMillis();
    }
}
