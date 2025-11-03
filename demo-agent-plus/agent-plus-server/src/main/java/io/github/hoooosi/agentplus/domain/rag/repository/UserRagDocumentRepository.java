package io.github.hoooosi.agentplus.domain.rag.repository;

import io.github.hoooosi.agentplus.domain.rag.model.UserRagDocumentEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserRagDocumentRepository extends MyBatisPlusExtRepository<UserRagDocumentEntity> {

}