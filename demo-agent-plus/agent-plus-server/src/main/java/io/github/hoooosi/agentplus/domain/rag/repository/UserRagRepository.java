package io.github.hoooosi.agentplus.domain.rag.repository;

import io.github.hoooosi.agentplus.domain.rag.model.UserRagEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRagRepository extends MyBatisPlusExtRepository<UserRagEntity> {

}