package io.github.hoooosi.agentplus.domain.rag.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 向量化状态枚举 */
@Getter
@AllArgsConstructor
public enum EmbeddingStatusEnum {

    UNINITIALIZED(0, "未初始化"),
    INITIALIZING(1, "初始化中"),
    INITIALIZED(2, "已初始化"),
    INITIALIZATION_FAILED(3, "初始化失败");

    private final Integer code;
    private final String description;

    /** 根据状态码获取枚举
     * @param code 状态码
     * @return 枚举值 */
    public static EmbeddingStatusEnum fromCode(Integer code) {
        if (code == null) {
            return UNINITIALIZED;
        }
        for (EmbeddingStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return UNINITIALIZED;
    }
}