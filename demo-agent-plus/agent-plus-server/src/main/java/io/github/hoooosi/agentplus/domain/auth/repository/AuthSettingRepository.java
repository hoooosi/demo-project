package io.github.hoooosi.agentplus.domain.auth.repository;

import io.github.hoooosi.agentplus.domain.auth.model.AuthSettingEntity;
import io.github.hoooosi.agentplus.infrastructure.repository.MyBatisPlusExtRepository;
import org.apache.ibatis.annotations.Mapper;

/** 认证配置Repository接口 */
@Mapper
public interface AuthSettingRepository extends MyBatisPlusExtRepository<AuthSettingEntity> {
}