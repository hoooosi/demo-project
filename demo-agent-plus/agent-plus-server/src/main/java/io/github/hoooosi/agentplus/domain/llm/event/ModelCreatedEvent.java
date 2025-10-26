package io.github.hoooosi.agentplus.domain.llm.event;


import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 模型创建事件 */
@Getter
public class ModelCreatedEvent extends ModelDomainEvent {
    /** 模型实体 */
    private final ModelEntity model;

    public ModelCreatedEvent(Long modelId, Long userId, ModelEntity model) {
        super(modelId, userId);
        this.model = model;
    }
}
