package io.github.hoooosi.agentplus.application.user.service;

import io.github.hoooosi.agentplus.domain.auth.constant.AuthFeatureKey;
import io.github.hoooosi.agentplus.domain.user.service.UserDomainService;
import io.github.hoooosi.agentplus.domain.auth.service.AuthSettingDomainService;
import io.github.hoooosi.agentplus.domain.user.service.model.UserEntity;
import io.github.hoooosi.agentplus.infrastructure.email.EmailService;
import io.github.hoooosi.agentplus.infrastructure.utils.JwtUtils;
import io.github.hoooosi.agentplus.infrastructure.verification.VerificationCodeService;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.LoginRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.RegisterRequest;
import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class LoginAppService {

    private final UserDomainService userDomainService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final AuthSettingDomainService authSettingDomainService;

    /** 用户登录，返回JWT令牌 */
    public String login(LoginRequest request) {
        // 检查普通登录是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.NORMAL_LOGIN)) {
            throw new BusinessException("普通登录已禁用");
        }

        UserEntity userEntity = userDomainService.login(request.getAccount(), request.getPassword());
        return JwtUtils.generateToken(userEntity.getId());
    }

    /** 用户注册 */
    public void register(RegisterRequest request) {
        // 检查用户注册是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.USER_REGISTER)) {
            throw new BusinessException("用户注册已禁用");
        }

        // 如果是邮箱注册，需要验证码
        if (StringUtils.hasText(request.getEmail()) && !StringUtils.hasText(request.getPhone())) {
            if (!StringUtils.hasText(request.getCode())) {
                throw new BusinessException("邮箱注册需要验证码");
            }

            boolean isValid = verificationCodeService.verifyCode(request.getEmail(), request.getCode());
            if (!isValid) {
                throw new BusinessException("验证码无效或已过期");
            }
        }

        userDomainService.register(
                request.getEmail(),
                request.getPhone(),
                request.getPassword());
    }

    /** 发送注册邮箱验证码 */
    public void sendEmailVerificationCode(String email, String captchaUuid, String captchaCode, String ip) {
        // 检查用户注册是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.USER_REGISTER)) {
            throw new BusinessException("用户注册已禁用");
        }

        // 检查邮箱是否已存在
        userDomainService.checkAccountExist(email);

        // 生成验证码并发送邮件
        String code = verificationCodeService.generateCode(email, captchaUuid, captchaCode, ip);
        emailService.sendVerificationCode(email, code);
    }

    /** 发送重置密码邮箱验证码 */
    public void sendResetPasswordCode(String email, String captchaUuid, String captchaCode, String ip) {
        // 检查普通登录是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.NORMAL_LOGIN)) {
            throw new BusinessException("普通登录已禁用，无法重置密码");
        }

        // 检查邮箱是否存在，不存在则抛出异常
        UserEntity user = userDomainService.findUserByAccount(email);
        if (user == null) {
            throw new BusinessException("该邮箱未注册");
        }

        // 生成验证码并发送邮件
        String code = verificationCodeService.generateCode(email, captchaUuid, captchaCode, ip,
                VerificationCodeService.BUSINESS_TYPE_RESET_PASSWORD);
        emailService.sendVerificationCode(email, code);
    }

    /** 验证邮箱验证码（注册） */
    public boolean verifyEmailCode(String email, String code) {
        return verificationCodeService.verifyCode(email, code);
    }

    /** 验证重置密码邮箱验证码 */
    public boolean verifyResetPasswordCode(String email, String code) {
        return verificationCodeService.verifyCode(email, code, VerificationCodeService.BUSINESS_TYPE_RESET_PASSWORD);
    }

    /** 重置密码 */
    public void resetPassword(String email, String newPassword, String code) {
        // 检查普通登录是否启用
        if (!authSettingDomainService.isFeatureEnabled(AuthFeatureKey.NORMAL_LOGIN)) {
            throw new BusinessException("普通登录已禁用，无法重置密码");
        }

        // 验证重置密码验证码
        boolean isValid = verifyResetPasswordCode(email, code);
        if (!isValid) {
            throw new BusinessException("验证码无效或已过期");
        }

        // 查找用户并重置密码
        UserEntity user = userDomainService.findUserByAccount(email);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新密码
        userDomainService.updatePassword(user.getId(), newPassword);
    }
}
