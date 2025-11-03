package io.github.hoooosi.agentplus.application.auth.dto;

import lombok.Data;

/** 登录方式DTO */
@Data
public class LoginMethodDTO {
    private Boolean enabled;
    private String name;
    private String provider;
}