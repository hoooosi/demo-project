package io.github.hoooosi.agentplus.interfaces.dto.users.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendResetPasswordCodeRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "图形验证码UUID不能为空")
    private String captchaUuid;

    @NotBlank(message = "图形验证码不能为空")
    private String captchaCode;

}
