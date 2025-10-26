package io.github.hoooosi.agentplus.application.user.assembler;

import io.github.hoooosi.agentplus.application.user.dto.UserSettingsDTO;
import io.github.hoooosi.agentplus.domain.user.service.model.UserSettingsEntity;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserSettingsUpdateRequest;
import org.springframework.beans.BeanUtils;

/** 用户设置转换器 */
public class UserSettingsAssembler {

    /** 实体转DTO */
    public static UserSettingsDTO toDTO(UserSettingsEntity entity) {
        if (entity == null) {
            return null;
        }
        UserSettingsDTO dto = new UserSettingsDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /** 请求转实体 */
    public static UserSettingsEntity toEntity(UserSettingsUpdateRequest request, Long userId) {
        if (request == null) {
            return null;
        }
        UserSettingsEntity entity = new UserSettingsEntity();
        entity.setSettingConfig(request.getSettingConfig());
        entity.setUserId(userId);
        return entity;
    }
}