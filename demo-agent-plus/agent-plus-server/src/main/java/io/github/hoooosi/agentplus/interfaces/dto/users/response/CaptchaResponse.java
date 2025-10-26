package io.github.hoooosi.agentplus.interfaces.dto.users.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String uuid;
    private String imageBase64;
}
