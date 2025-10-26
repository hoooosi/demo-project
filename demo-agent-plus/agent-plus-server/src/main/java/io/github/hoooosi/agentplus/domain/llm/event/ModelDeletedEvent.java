package io.github.hoooosi.agentplus.domain.llm.event;

import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import lombok.Getter;


/** 模型创建事件 */
@Getter
public class ModelDeletedEvent extends ModelDomainEvent {
    public ModelDeletedEvent(Long modelId, Long userId) {
        super(modelId, userId);
    }
}
