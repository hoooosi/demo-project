package io.github.hoooosi.agentplus.domain.llm.event;

import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import lombok.Getter;

/** 模型更新事件 */
@Getter
public class ModelUpdatedEvent extends ModelDomainEvent {
    /** 更新后的模型实体 */
    private final ModelEntity model;

    public ModelUpdatedEvent(Long modelId, Long userId, ModelEntity model) {
        super(modelId, userId);
        this.model = model;
    }
}
