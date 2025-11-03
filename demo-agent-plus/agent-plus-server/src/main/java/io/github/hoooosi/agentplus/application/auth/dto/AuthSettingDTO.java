package io.github.hoooosi.agentplus.application.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/** 认证配置DTO */
@Data
public class AuthSettingDTO {
    private String id;
    private String featureType;
    private String featureKey;
    private String featureName;
    private Boolean enabled;
    private Map<String, Object> configData;
    private Integer displayOrder;
    private String description;
    private Long createdAt;
    private Long updatedAt;
}