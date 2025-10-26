package io.github.hoooosi.agentplus.domain.user.service.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.domain.llm.config.ProviderConfig;
import io.github.hoooosi.agentplus.domain.user.service.protocol.ProviderProtocol;
import io.github.hoooosi.agentplus.infrastructure.converter.ProviderConfigConverter;
import io.github.hoooosi.agentplus.infrastructure.converter.ProviderProtocolConverter;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/** 服务提供商领域模型 */
@EqualsAndHashCode(callSuper = true)
@TableName("providers")
@Data
public class ProviderEntity extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private Long id;
    private Long userId;
    @TableField(typeHandler = ProviderProtocolConverter.class)
    private ProviderProtocol protocol;
    private String name;
    private String description;
    @TableField(typeHandler = ProviderConfigConverter.class)
    private ProviderConfig config;
    private Boolean isOfficial;
    private Boolean status;

    public void isActive() {
        if (!status) {
            throw new BusinessException("服务商未激活");
        }
    }

    public void isAvailable(String userId) {
        if (!isOfficial && !Objects.equals(this.getUserId(), userId)) {
            throw new BusinessException("模型未找到");
        }
    }

}
