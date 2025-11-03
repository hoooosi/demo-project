package io.github.hoooosi.agentplus.interfaces.api.portal.auth;

import io.github.hoooosi.agentplus.application.auth.dto.AuthConfigDTO;
import io.github.hoooosi.agentplus.application.auth.service.AuthSettingAppService;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 认证配置控制器（用户端） */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthConfigController {

    private final AuthSettingAppService authSettingAppService;

    /** 获取可用的认证配置 */
    @GetMapping("/config")
    public Result<AuthConfigDTO> getAuthConfig() {
        AuthConfigDTO config = authSettingAppService.getAuthConfig();
        return Result.success(config);
    }
}