package io.github.hoooosi.agentplus.domain.user.repository;

import io.github.hoooosi.agentplus.domain.user.model.AccountEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 账户仓储接口 */
@Mapper
public interface AccountRepository extends MyBatisPlusExtRepository<AccountEntity> {
}