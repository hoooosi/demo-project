package io.github.hoooosi.agentplus.domain.memory.repository;

import io.github.hoooosi.agentplus.domain.memory.model.MemoryItemEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** memory_items 表数据访问（基于 MyBatis-Plus 提供通用 CRUD） */
@Mapper
public interface MemoryItemRepository extends MyBatisPlusExtRepository<MemoryItemEntity> {
}
