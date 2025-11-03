package io.github.hoooosi.agentplus.interfaces.api.portal.memory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.agentplus.application.memory.dto.MemoryItemDTO;
import io.github.hoooosi.agentplus.application.memory.service.MemoryAppService;
import io.github.hoooosi.agentplus.infrastructure.auth.UserContext;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import io.github.hoooosi.agentplus.interfaces.dto.memory.CreateMemoryRequest;
import io.github.hoooosi.agentplus.interfaces.dto.memory.QueryMemoryRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 用户记忆管理（Portal） */
@RestController
@RequestMapping("/portal/memory")
@Validated
@AllArgsConstructor
public class PortalMemoryController {

    private final MemoryAppService memoryAppService;

    /** 分页列出当前用户的记忆（可选类型过滤） */
    @GetMapping("/items")
    public Result<Page<MemoryItemDTO>> list(QueryMemoryRequest request) {
        Long userId = UserContext.getCurrentUserId();
        Page<MemoryItemDTO> page = memoryAppService.listUserMemories(userId, request);
        return Result.success(page);
    }

    /** 手动新增记忆（立即入库并向量化） */
    @PostMapping("/items")
    public Result<?> create(@RequestBody @Valid CreateMemoryRequest request) {
        Long userId = UserContext.getCurrentUserId();
        memoryAppService.createMemory(userId, request);
        return Result.success();
    }

    /** 归档（软删除）记忆 */
    @DeleteMapping("/items/{itemId}")
    public Result<Void> delete(@PathVariable Long itemId) {
        Long userId = UserContext.getCurrentUserId();
        boolean ok = memoryAppService.deleteMemory(userId, itemId);
        return ok ? Result.success() : Result.notFound("记忆不存在或无权限");
    }
}
