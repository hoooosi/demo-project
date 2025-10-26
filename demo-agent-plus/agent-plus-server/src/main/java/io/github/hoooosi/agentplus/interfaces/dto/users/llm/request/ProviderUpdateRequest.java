package io.github.hoooosi.agentplus.interfaces.dto.users.llm.request;

import io.github.hoooosi.agentplus.domain.llm.config.ProviderConfig;
import io.github.hoooosi.agentplus.domain.user.service.protocol.ProviderProtocol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProviderUpdateRequest {
    /** 服务商ID */
    private Long id;
    /** 服务商描述 */
    private String description;
    /** 服务商协议 */
    @NotNull(message = "协议不可为空")
    private ProviderProtocol protocol;
    /** 服务商名称 */
    @NotBlank(message = "名称不可为空")
    private String name;
    /** 服务商配置 */
    private ProviderConfig config;
    /** 服务商状态 */
    private Boolean status;
}
