package io.github.hoooosi.agentplus.domain.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.domain.user.model.config.UserSettingsConfig;
import io.github.hoooosi.agentplus.infrastructure.converter.UserSettingsConfigConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

/** 用户设置领域模型 */
@EqualsAndHashCode(callSuper = true)
@TableName("user_settings")
@Data
@Accessors(chain = true)
public class UserSettingsEntity extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 设置配置 */
    @TableField(typeHandler = UserSettingsConfigConverter.class, jdbcType = JdbcType.OTHER)
    private UserSettingsConfig settingConfig;

    /** 获取默认模型ID */
    public Long getDefaultModelId() {
        if (settingConfig == null) {
            return null;
        }
        return settingConfig.getDefaultModel();
    }

    /** 设置默认模型ID */
    public void setDefaultModelId(Long modelId) {
        if (settingConfig == null) {
            settingConfig = new UserSettingsConfig();
        }
        settingConfig.setDefaultModel(modelId);
    }
}
