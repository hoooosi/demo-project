package io.github.hoooosi.agentplus.domain.llm.repository;

import io.github.hoooosi.agentplus.domain.llm.model.ModelEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 模型仓储接口 */
@Mapper
public interface ModelRepository extends MyBatisPlusExtRepository<ModelEntity> {
}
