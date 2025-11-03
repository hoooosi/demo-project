package io.github.hoooosi.agentplus.interfaces.dto.usage.request;


import io.github.hoooosi.agentplus.interfaces.dto.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/** 查询使用记录请求 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryUsageRecordRequest extends Page {

    /** 用户ID */
    private Long userId;

    /** 商品ID */
    private String productId;

    /** 请求ID */
    private String requestId;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;
}