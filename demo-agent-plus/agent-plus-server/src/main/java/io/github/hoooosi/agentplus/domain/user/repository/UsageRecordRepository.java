package io.github.hoooosi.agentplus.domain.user.repository;

import io.github.hoooosi.agentplus.domain.user.model.UsageRecordEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 用量记录仓储接口 */
@Mapper
public interface UsageRecordRepository extends MyBatisPlusExtRepository<UsageRecordEntity> {
}