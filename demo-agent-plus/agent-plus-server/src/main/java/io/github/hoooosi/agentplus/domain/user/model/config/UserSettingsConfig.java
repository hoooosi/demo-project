package io.github.hoooosi.agentplus.domain.user.model.config;

import lombok.Data;

import java.io.Serializable;

/** 用户设置配置 */
@Data
public class UserSettingsConfig implements Serializable {
    /** 默认聊天模型ID */
    private Long defaultModel;

    /** 默认OCR模型ID */
    private Long defaultOcrModel;

    /** 默认嵌入模型ID */
    private Long defaultEmbeddingModel;

    /** 降级配置 */
    private FallbackConfig fallbackConfig;
}