package io.github.hoooosi.agentplus.application.memory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.agentplus.application.memory.assembler.MemoryAssembler;
import io.github.hoooosi.agentplus.application.memory.assembler.MemoryCommandAssembler;
import io.github.hoooosi.agentplus.application.memory.dto.MemoryItemDTO;
import io.github.hoooosi.agentplus.domain.memory.model.CandidateMemory;
import io.github.hoooosi.agentplus.domain.memory.model.MemoryItemEntity;
import io.github.hoooosi.agentplus.domain.memory.service.MemoryDomainService;
import io.github.hoooosi.agentplus.interfaces.dto.memory.CreateMemoryRequest;
import io.github.hoooosi.agentplus.interfaces.dto.memory.QueryMemoryRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryAppService {

    private final MemoryDomainService memoryDomainService;

    public MemoryAppService(MemoryDomainService memoryDomainService) {
        this.memoryDomainService = memoryDomainService;
    }

    /** 分页列出用户记忆 */
    public Page<MemoryItemDTO> listUserMemories(Long  userId, QueryMemoryRequest request) {
        int pageNo = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        Page<MemoryItemEntity> page = memoryDomainService.pageMemories(userId, request.getType(), pageNo, pageSize);
        Page<MemoryItemDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(MemoryAssembler.toDTOs(page.getRecords()));
        return dtoPage;
    }

    /** 手动创建记忆 */
    public List<String> createMemory(Long userId, CreateMemoryRequest request) {
        CandidateMemory cm = MemoryCommandAssembler.toCandidate(request);
        List<CandidateMemory> list = new ArrayList<>();
        list.add(cm);
        return memoryDomainService.saveMemories(userId, null, list);
    }

    /** 归档（软删除）记忆 */
    public boolean deleteMemory(Long  userId, Long  itemId) {
        return memoryDomainService.delete(userId, itemId);
    }
}
