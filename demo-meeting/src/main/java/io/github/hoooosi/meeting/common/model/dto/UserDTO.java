package io.github.hoooosi.meeting.common.model.dto;

public class UserDTO {

    public record RegisterDTO(
            String checkCode,
            String checkCodeKey,
            String email,
            String password,
            String nickName
    ) {
    }

    public record LoginDTO(
            String email,
            String password
    ) {
    }

}
