package io.github.hoooosi.agentplus.application.auth.dto;

import lombok.Data;

import java.util.Map;

/** 更新认证配置请求DTO */
@Data
public class UpdateAuthSettingRequest {
    private String featureName;
    private Boolean enabled;
    private Map<String, Object> configData;
    private Integer displayOrder;
    private String description;
}