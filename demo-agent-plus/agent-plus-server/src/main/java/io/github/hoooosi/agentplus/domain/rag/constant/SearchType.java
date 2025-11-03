package io.github.hoooosi.agentplus.domain.rag.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 检索类型枚举 定义混合检索系统中的不同检索方式 */
@Getter
@AllArgsConstructor
public enum SearchType {

    VECTOR("vector", "向量检索"),
    KEYWORD("keyword", "关键词检索"),
    HYBRID("hybrid", "混合检索");

    private final String code;
    private final String description;

    /** 根据代码获取枚举
     * @param code 检索类型代码
     * @return 对应的枚举，未找到则返回null */
    public static SearchType fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (SearchType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}