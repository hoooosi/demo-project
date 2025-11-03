package io.github.hoooosi.agentplus.interfaces.dto.users.request;

import io.github.hoooosi.agentplus.domain.user.model.config.UserSettingsConfig;
import lombok.Data;


@Data
public class UserSettingsUpdateRequest {
    private UserSettingsConfig settingConfig;
}
