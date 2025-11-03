package io.github.hoooosi.agentplus.interfaces.dto.memory;

import io.github.hoooosi.agentplus.interfaces.dto.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 查询记忆的请求参数（分页 + 过滤） */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryMemoryRequest extends Page {
    private String type; // 可选：PROFILE/TASK/FACT/EPISODIC
}
