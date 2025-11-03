package io.github.hoooosi.agentplus.application.auth.dto;

import lombok.Data;

import java.util.Map;

/** 认证配置响应DTO */
@Data
public class AuthConfigDTO {
    private Map<String, LoginMethodDTO> loginMethods;
    private Boolean registerEnabled;
}