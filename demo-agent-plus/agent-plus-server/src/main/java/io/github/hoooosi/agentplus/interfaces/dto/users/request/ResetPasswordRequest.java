package io.github.hoooosi.agentplus.interfaces.dto.users.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应在6-20位之间")
    private String newPassword;

    @NotBlank(message = "验证码不能为空")
    private String code;
}
