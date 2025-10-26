package io.github.hoooosi.agentplus.domain.llm.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** 模型批量删除事件 */
@AllArgsConstructor
@Getter
public class ModelsBatchDeletedEvent {

    /** 删除项列表 */
    private final List<ModelDeleteItem> deleteItems;
    /** 用户ID */
    private final Long userId;

    /** 模型删除项 */
    @Getter
    @AllArgsConstructor
    public static class ModelDeleteItem {
        private final Long modelId;
        private final Long userId;
    }
}
