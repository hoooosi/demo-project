package io.github.hoooosi.agentplus.application.user.dto;

import io.github.hoooosi.agentplus.domain.user.model.config.UserSettingsConfig;
import lombok.Data;

@Data
public class UserSettingsDTO {
    /** 主键ID */
    private String id;
    /** 用户ID */
    private String userId;
    /** 配置 */
    private UserSettingsConfig settingConfig;
}
