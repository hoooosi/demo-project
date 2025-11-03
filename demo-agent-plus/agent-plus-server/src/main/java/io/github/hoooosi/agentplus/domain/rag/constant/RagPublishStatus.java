package io.github.hoooosi.agentplus.domain.rag.constant;

import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** RAG版本发布状态枚举 */
@Getter
@AllArgsConstructor
public enum RagPublishStatus {

    REVIEWING(1, "审核中"),
    PUBLISHED(2, "已发布"),
    REJECTED(3, "拒绝"),
    REMOVED(4, "已下架");

    private final Integer code;
    private final String description;

    /** 根据状态码获取枚举值 */
    public static RagPublishStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (RagPublishStatus status : RagPublishStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        throw new BusinessException("INVALID_RAG_STATUS_CODE", "无效的RAG发布状态码: " + code);
    }
}