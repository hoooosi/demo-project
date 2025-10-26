package io.github.hoooosi.agentplus.domain.auth.model;



import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.infrastructure.converter.MapConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/** 认证配置实体类 */
@EqualsAndHashCode(callSuper = true)
@TableName("auth_settings")
@Data
public class AuthSettingEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String featureType;
    private String featureKey;
    private String featureName;
    private Boolean enabled;
    @TableField(typeHandler = MapConverter.class)
    private Map<String, Object> configData;
    private Integer displayOrder;
    private String description;
}