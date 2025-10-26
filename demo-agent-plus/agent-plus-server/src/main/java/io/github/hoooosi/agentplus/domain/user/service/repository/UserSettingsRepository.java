package io.github.hoooosi.agentplus.domain.user.service.repository;

import io.github.hoooosi.agentplus.domain.user.service.model.UserSettingsEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 用户设置仓储接口 */
@Mapper
public interface UserSettingsRepository extends MyBatisPlusExtRepository<UserSettingsEntity> {
}