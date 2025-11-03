package io.github.hoooosi.agentplus.application.user.service;

import io.github.hoooosi.agentplus.application.user.assembler.UserSettingsAssembler;
import io.github.hoooosi.agentplus.application.user.dto.UserSettingsDTO;
import io.github.hoooosi.agentplus.domain.user.service.UserSettingsDomainService;
import io.github.hoooosi.agentplus.domain.user.model.UserSettingsEntity;
import io.github.hoooosi.agentplus.domain.user.model.config.FallbackConfig;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserSettingsUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserSettingsAppService {
    private final UserSettingsDomainService userSettingsDomainService;

    /** 获取用户设置
     * @param userId 用户ID
     * @return 用户设置DTO */
    public UserSettingsDTO getUserSettings(Long userId) {
        UserSettingsEntity entity = userSettingsDomainService.getUserSettings(userId);
        return UserSettingsAssembler.toDTO(entity);
    }

    /** 更新用户设置
     * @param request 更新请求
     * @param userId 用户ID
     * @return 更新后的用户设置DTO */
    public UserSettingsDTO updateUserSettings(UserSettingsUpdateRequest request, Long userId) {
        UserSettingsEntity entity = UserSettingsAssembler.toEntity(request, userId);
        userSettingsDomainService.update(entity);

        return UserSettingsAssembler.toDTO(entity);
    }

    /** 获取用户默认模型ID
     * @param userId 用户ID
     * @return 默认模型ID */
    public Long getUserDefaultModelId(Long userId) {
        return userSettingsDomainService.getUserDefaultModelId(userId);
    }

    /** 获取用户降级链配置
     * @param userId 用户ID
     * @return 降级模型ID列表，如果未启用降级则返回null */
    public List<String> getUserFallbackChain(Long userId) {
        UserSettingsEntity settings = userSettingsDomainService.getUserSettings(userId);
        if (settings == null || settings.getSettingConfig() == null) {
            return null;
        }

        FallbackConfig fallbackConfig = settings.getSettingConfig().getFallbackConfig();
        if (fallbackConfig == null || !fallbackConfig.isEnabled() || fallbackConfig.getFallbackChain().isEmpty()) {
            return null;
        }

        return fallbackConfig.getFallbackChain();
    }
}
