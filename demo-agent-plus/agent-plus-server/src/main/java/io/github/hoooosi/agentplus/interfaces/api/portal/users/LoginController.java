package io.github.hoooosi.agentplus.interfaces.api.portal.users;

import io.github.hoooosi.agentplus.application.user.service.LoginAppService;
import io.github.hoooosi.agentplus.infrastructure.utils.CaptchaUtils;
import io.github.hoooosi.agentplus.interfaces.api.common.Result;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.*;
import io.github.hoooosi.agentplus.interfaces.dto.users.response.CaptchaResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
@AllArgsConstructor
public class LoginController {

    private final LoginAppService loginAppService;

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginRequest request) {
        String token = loginAppService.login(request);
        return Result.success("登录成功", Map.of("token", token));
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<?> register(@RequestBody @Validated RegisterRequest request) {
        loginAppService.register(request);
        return Result.success().message("注册成功");
    }

    /** 获取验证码 */
    @PostMapping("/get-captcha")
    public Result<CaptchaResponse> getCaptcha(@RequestBody(required = false) GetCaptchaRequest request) {
        CaptchaUtils.CaptchaResult captchaResult = CaptchaUtils.generateCaptcha();
        CaptchaResponse response = new CaptchaResponse(captchaResult.getUuid(), captchaResult.getImageBase64());
        return Result.success(response);
    }

    /** 重置密码 */
    @PostMapping("/reset-password")
    public Result<?> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {
        loginAppService.resetPassword(request.getEmail(), request.getNewPassword(), request.getCode());
        return Result.success().message("密码重置成功");
    }

    /** 发送邮箱验证码接口 */
    @PostMapping("/send-email-code")
    public Result<?> sendEmailCode(@RequestBody @Validated SendEmailCodeRequest request,
                                   HttpServletRequest httpRequest) {
        // 获取客户端IP
        String clientIp = getClientIp(httpRequest);
        loginAppService.sendEmailVerificationCode(request.getEmail(), request.getCaptchaUuid(),
                request.getCaptchaCode(), clientIp);
        return Result.success().message("验证码已发送，请查收邮件");
    }

    /** 发送重置密码邮箱验证码接口 */
    @PostMapping("/send-reset-password-code")
    public Result<?> sendResetPasswordCode(@RequestBody @Validated SendResetPasswordCodeRequest request,
                                           HttpServletRequest httpRequest) {
        // 获取客户端IP
        String clientIp = getClientIp(httpRequest);

        loginAppService.sendResetPasswordCode(request.getEmail(), request.getCaptchaUuid(), request.getCaptchaCode(),
                clientIp);
        return Result.success().message("验证码已发送，请查收邮件");
    }

    /** 验证邮箱验证码接口 */
    @PostMapping("/verify-email-code")
    public Result<Boolean> verifyEmailCode(@RequestBody @Validated VerifyEmailCodeRequest request) {
        boolean isValid = loginAppService.verifyEmailCode(request.getEmail(), request.getCode());
        if (isValid) {
            return Result.success(true).message("验证码验证成功");
        } else {
            return Result.error(403, "验证码无效或已过期");
        }
    }

    /** 获取客户端IP */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
