package io.github.hoooosi.agentplus.domain.llm.repository;

import io.github.hoooosi.agentplus.domain.llm.model.ProviderEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 服务提供商仓储接口 */
@Mapper
public interface ProviderRepository extends MyBatisPlusExtRepository<ProviderEntity> {

}