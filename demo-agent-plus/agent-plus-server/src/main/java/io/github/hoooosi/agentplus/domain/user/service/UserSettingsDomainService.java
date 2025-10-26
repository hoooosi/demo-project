package io.github.hoooosi.agentplus.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.hoooosi.agentplus.domain.user.service.model.UserSettingsEntity;
import io.github.hoooosi.agentplus.domain.user.service.model.config.FallbackConfig;
import io.github.hoooosi.agentplus.domain.user.service.repository.UserSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 用户设置领域服务 */
@Service
@AllArgsConstructor
public class UserSettingsDomainService {
    private final UserSettingsRepository userSettingsRepository;

    /** 获取用户设置
     * @param userId 用户ID
     * @return 用户设置实体 */
    public UserSettingsEntity getUserSettings(Long userId) {
        return userSettingsRepository.selectOne(Wrappers.<UserSettingsEntity>lambdaQuery()
                .eq(UserSettingsEntity::getUserId, userId));
    }

    /** 更新用户设置
     * @param userSettings 用户设置实体 */
    public void update(UserSettingsEntity userSettings) {
        userSettingsRepository.checkedUpdate(userSettings, Wrappers.<UserSettingsEntity>lambdaQuery()
                .eq(UserSettingsEntity::getUserId, userSettings.getUserId()));
    }

    /** 获取用户默认模型ID
     * @param userId 用户ID
     * @return 默认模型ID */
    public Long getUserDefaultModelId(Long userId) {
        UserSettingsEntity settings = getUserSettings(userId);
        return settings != null ? settings.getDefaultModelId() : null;
    }

    /** 获取用户降级链配置
     * @param userId 用户ID
     * @return 降级模型ID列表，如果未启用降级则返回null */
    public List<String> getUserFallbackChain(Long userId) {
        UserSettingsEntity settings = getUserSettings(userId);
        if (settings == null || settings.getSettingConfig() == null) {
            return new ArrayList<>();
        }

        FallbackConfig fallbackConfig = settings.getSettingConfig().getFallbackConfig();
        if (fallbackConfig == null || !fallbackConfig.isEnabled() || fallbackConfig.getFallbackChain().isEmpty()) {
            return new ArrayList<>();
        }

        return fallbackConfig.getFallbackChain();
    }

    /** 设置用户默认模型ID
     * @param userId 用户ID
     * @param modelId 模型ID */
    public void setUserDefaultModelId(Long userId, Long modelId) {
        UserSettingsEntity settings = getUserSettings(userId);
        if (settings == null) {
            // 创建新的用户设置
            settings = new UserSettingsEntity();
            settings.setUserId(userId);
            settings.setDefaultModelId(modelId);
            userSettingsRepository.insert(settings);
        } else {
            // 更新现有设置
            settings.setDefaultModelId(modelId);
            userSettingsRepository.checkedUpdate(settings, Wrappers.<UserSettingsEntity>lambdaQuery()
                    .eq(UserSettingsEntity::getUserId, userId));
        }
    }
}
