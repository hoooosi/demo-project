package io.github.hoooosi.agentplus.domain.llm.model;

import io.github.hoooosi.agentplus.domain.llm.model.config.ProviderConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 服务提供商聚合根 */
@AllArgsConstructor
@Data
public class ProviderAggregate {

    private ProviderEntity entity;
    private List<ModelEntity> models = new ArrayList<>();

    /** 添加模型 */
    public void addModel(ModelEntity model) {
        models.add(model);
    }

    /** 获取服务商配置（解密版本） */
    public ProviderConfig getConfig() {
        return entity.getConfig(); // 已解密
    }

    /** 设置服务商配置（会自动加密） */
    public void setConfig(ProviderConfig config) {
        entity.setConfig(config); // 会自动加密
    }

    public boolean getStatus() {
        return entity.getStatus();
    }

    public String getName() {
        return entity.getName();
    }
}
