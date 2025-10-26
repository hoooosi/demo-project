package io.github.hoooosi.agentplus.application.llm.dto;

import io.github.hoooosi.agentplus.domain.llm.config.ProviderConfig;
import io.github.hoooosi.agentplus.domain.user.service.protocol.ProviderProtocol;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 服务提供商DTO */
@Data
@Accessors(chain = true)
public class ProviderDTO {
    /** 服务商id */
    private Long id;
    /** 服务商协议 */
    private ProviderProtocol protocol;
    /** 服务商名称 */
    private String name;
    /** 服务商描述 */
    private String description;
    /** 服务商配置 */
    private ProviderConfig config;
    /** 是否官方 */
    private Boolean isOfficial;
    /** 服务商状态 */
    private Boolean status;
    /** 创建时间 */
    private Long createdAt;
    /** 更新时间 */
    private Long updatedAt;
    /** 模型列表 */
    private List<ModelDTO> models = new ArrayList<>();

    /** 脱敏配置信息（用于返回前端） */
    public void maskSensitiveInfo() {
        if (this.config != null) {
            // 如果有API Key，则脱敏处理
            if (this.config.getApiKey() != null && !this.config.getApiKey().isEmpty()) {
                this.config.setApiKey("***********");
            }
        }
    }
}
