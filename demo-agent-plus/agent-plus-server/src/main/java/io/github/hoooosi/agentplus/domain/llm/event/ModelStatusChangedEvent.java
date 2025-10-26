package io.github.hoooosi.agentplus.domain.llm.event;

import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import lombok.Getter;

@Getter
public class ModelStatusChangedEvent extends ModelDomainEvent {
    /** 变更后的模型实体 */
    private final ModelEntity model;

    /** 新状态，true=启用，false=禁用 */
    private final boolean enabled;

    /** 状态变更原因 */
    private final String reason;

    public ModelStatusChangedEvent(Long modelId, Long userId, ModelEntity model, boolean enabled, String reason) {
        super(modelId, userId);
        this.model = model;
        this.enabled = enabled;
        this.reason = reason;
    }
}
