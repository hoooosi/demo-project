package io.github.hoooosi.agentplus.interfaces.api.portal.users;

import io.github.hoooosi.agentplus.application.user.service.SsoAppService;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sso")
@AllArgsConstructor
public class SsoController {

    private final SsoAppService ssoAppService;

    /** 获取SSO登录URL
     * @param provider SSO提供商
     * @param redirectUrl 登录成功后的回调地址
     * @return 登录URL */
    @GetMapping("/{provider}/login")
    public Result<Map<String, String>> getSsoLoginUrl(@PathVariable String provider,
                                                      @RequestParam(required = false) String redirectUrl) {
        String loginUrl = ssoAppService.getSsoLoginUrl(provider, redirectUrl);
        return Result.success(Map.of("loginUrl", loginUrl));
    }

    /** SSO登录回调处理
     * @param provider SSO提供商
     * @param code 授权码
     * @return 登录token */
    @GetMapping("/{provider}/callback")
    public Result<Map<String, Object>> handleSsoCallback(@PathVariable String provider, @RequestParam String code) {
        String token = ssoAppService.handleSsoCallback(provider, code);
        return Result.success("登录成功", Map.of("token", token));
    }
}
