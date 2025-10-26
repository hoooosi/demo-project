package io.github.hoooosi.agentplus.domain.user.service.repository;

import io.github.hoooosi.agentplus.domain.user.service.model.UserEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 模型仓储接口 */
@Mapper
public interface UserRepository extends MyBatisPlusExtRepository<UserEntity> {
}
