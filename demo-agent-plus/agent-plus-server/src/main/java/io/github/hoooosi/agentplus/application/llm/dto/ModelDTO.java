package io.github.hoooosi.agentplus.application.llm.dto;

import io.github.hoooosi.agentplus.domain.llm.enums.ModelType;
import lombok.Data;
import lombok.experimental.Accessors;

/** 模型数据传输对象 */
@Data
@Accessors(chain = true)
public class ModelDTO {

    /** 模型id */
    private Long id;
    /** 用户id */
    private Long userId;
    /** 服务商id */
    private Long providerId;
    /** 服务商名称 */
    private String providerName; // 额外添加，便于前端显示
    /** 模型id */
    private String modelId;
    /** 模型名称 */
    private String name;
    /** 模型描述 */
    private String description;
    /** 模型类型 */
    private ModelType type;
    /** 模型部署名称 */
    private String modelEndpoint;
    /** 是否官方 */
    private Boolean isOfficial;
    /** 模型状态 */
    private Boolean status;
    /** 创建时间 */
    private Long createdAt;
    /** 更新时间 */
    private Long updatedAt;
}