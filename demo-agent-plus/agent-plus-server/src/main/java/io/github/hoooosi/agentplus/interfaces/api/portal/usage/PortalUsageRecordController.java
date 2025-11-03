package io.github.hoooosi.agentplus.interfaces.api.portal.usage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.agentplus.application.usage.dto.UsageRecordDTO;
import io.github.hoooosi.agentplus.application.usage.service.UsageRecordAppService;
import io.github.hoooosi.agentplus.infrastructure.auth.UserContext;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import io.github.hoooosi.agentplus.interfaces.dto.usage.request.QueryUsageRecordRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

/** 使用记录控制层 提供使用记录查询的API接口 */
@RestController
@RequestMapping("/usage-records")
@AllArgsConstructor
public class PortalUsageRecordController {

    private final UsageRecordAppService usageRecordAppService;

    /** 根据ID获取使用记录
     * @param recordId 记录ID
     * @return 使用记录信息 */
    @GetMapping("/{recordId}")
    public Result<UsageRecordDTO> getUsageRecordById(@PathVariable String recordId) {
        UsageRecordDTO record = usageRecordAppService.getUsageRecordById(recordId);
        return Result.success(record);
    }

    /** 按条件查询当前用户使用记录
     * @param request 查询参数
     * @return 使用记录分页列表 */
    @GetMapping
    public Result<Page<UsageRecordDTO>> queryUsageRecords(QueryUsageRecordRequest request) {
        // 前台API只能查询当前用户的记录，防止越权
        Long userId = UserContext.getCurrentUserId();
        request.setUserId(userId);
        Page<UsageRecordDTO> records = usageRecordAppService.queryUsageRecords(request);
        return Result.success(records);
    }

    /** 获取当前用户的总消费金额
     * @return 总消费金额 */
    @GetMapping("/current/total-cost")
    public Result<BigDecimal> getCurrentUserTotalCost() {
        Long userId = UserContext.getCurrentUserId();
        BigDecimal totalCost = usageRecordAppService.getUserTotalCost(userId);
        return Result.success(totalCost);
    }
}