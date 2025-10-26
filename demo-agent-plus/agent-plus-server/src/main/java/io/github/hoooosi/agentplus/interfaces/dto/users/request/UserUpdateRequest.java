package io.github.hoooosi.agentplus.interfaces.dto.users.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank(message = "昵称不可未空")
    private String nickname;
}
