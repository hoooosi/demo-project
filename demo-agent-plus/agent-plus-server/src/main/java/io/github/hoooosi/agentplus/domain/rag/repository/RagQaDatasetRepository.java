package io.github.hoooosi.agentplus.domain.rag.repository;

import io.github.hoooosi.agentplus.domain.rag.model.RagQaDatasetEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RagQaDatasetRepository extends MyBatisPlusExtRepository<RagQaDatasetEntity> {

}
