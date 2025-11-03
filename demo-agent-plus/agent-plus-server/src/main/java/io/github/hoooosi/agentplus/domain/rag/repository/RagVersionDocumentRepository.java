package io.github.hoooosi.agentplus.domain.rag.repository;

import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;
import io.github.hoooosi.agentplus.domain.rag.model.RagVersionDocumentEntity;

/** RAG版本文档单元仓储接口 */
@Mapper
public interface RagVersionDocumentRepository extends MyBatisPlusExtRepository<RagVersionDocumentEntity> {

}