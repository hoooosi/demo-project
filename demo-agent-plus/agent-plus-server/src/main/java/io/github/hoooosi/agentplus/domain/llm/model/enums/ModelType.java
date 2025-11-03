package io.github.hoooosi.agentplus.domain.llm.model.enums;


import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.Getter;

/** 模型类型枚举 */
@Getter
public enum ModelType {

    CHAT("CHAT", "对话模型"), EMBEDDING("EMBEDDING", "嵌入模型");

    private final String code;
    private final String description;

    ModelType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ModelType fromCode(String code) {
        for (ModelType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new BusinessException("Unknown model type code: " + code);
    }
}