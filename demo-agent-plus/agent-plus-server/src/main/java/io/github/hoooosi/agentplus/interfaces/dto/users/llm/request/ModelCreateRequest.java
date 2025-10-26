package io.github.hoooosi.agentplus.interfaces.dto.users.llm.request;

import io.github.hoooosi.agentplus.domain.llm.enums.ModelType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 模型创建请求 */
@Data
public class ModelCreateRequest {
    /** 服务商ID */
    private Long providerId;
    /** 模型id */
    @NotBlank(message = "模型id不可为空")
    private String modelId;
    /** 模型名称 */
    @NotBlank(message = "名称不可为空")
    private String name;
    /** 模型描述 */
    private String description;
    /** 模型类型 */
    private ModelType type;
    /** 模型部署名称 */
    private String modelEndpoint;
}
