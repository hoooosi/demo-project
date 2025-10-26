package io.github.hoooosi.agentplus.interfaces.dto.users.llm.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 模型更新请求 */
@Data
public class ModelUpdateRequest {
    /** 模型ID */
    private Long id;
    /** 模型id */
    @NotBlank(message = "模型id不可为空")
    private String modelId;
    /** 模型名称 */
    @NotBlank(message = "名称不可为空")
    private String name;
    /** 模型描述 */
    private String description;
    /** 模型部署名称 */
    private String modelEndpoint;
}
