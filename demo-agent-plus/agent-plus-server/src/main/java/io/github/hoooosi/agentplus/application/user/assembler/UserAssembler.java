package io.github.hoooosi.agentplus.application.user.assembler;

import io.github.hoooosi.agentplus.application.user.dto.UserDTO;
import io.github.hoooosi.agentplus.domain.user.model.UserEntity;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.RegisterRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

public class UserAssembler {
    public static UserDTO toDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();

        BeanUtils.copyProperties(userEntity, userDTO);
        return userDTO;
    }

    public static UserEntity toEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDTO, userEntity);
        return userEntity;
    }

    public static UserEntity toEntity(RegisterRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registerRequest, userEntity);
        return userEntity;
    }

    public static UserEntity toEntity(UserUpdateRequest userUpdateRequest, Long userId) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userUpdateRequest, userEntity);
        userEntity.setId(userId);
        return userEntity;
    }
}
