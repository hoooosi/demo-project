package io.github.hoooosi.agentplus.application.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.agentplus.application.user.assembler.UserAssembler;
import io.github.hoooosi.agentplus.application.user.dto.UserDTO;
import io.github.hoooosi.agentplus.domain.user.service.UserDomainService;
import io.github.hoooosi.agentplus.domain.user.service.model.UserEntity;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.ChangePasswordRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.QueryUserRequest;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.UserUpdateRequest;
import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserAppService {
    private final UserDomainService userDomainService;

    /** 获取用户信息 */
    public UserDTO getUserInfo(Long id) {
        UserEntity userEntity = userDomainService.getUserInfo(id);
        return UserAssembler.toDTO(userEntity);
    }

    /** 修改用户信息 */
    public void updateUserInfo(UserUpdateRequest userUpdateRequest, Long userId) {
        UserEntity user = UserAssembler.toEntity(userUpdateRequest, userId);
        userDomainService.updateUserInfo(user);
    }

    /** 修改用户密码
     *
     * @param request 修改密码请求
     * @param userId 用户ID */
    public void changePassword(ChangePasswordRequest request, Long userId) {
        // 验证确认密码
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不一致");
        }

        // 调用领域服务修改密码
        userDomainService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
    }

    /** 分页获取用户列表
     *
     * @param queryUserRequest 查询条件
     * @return 用户分页数据 */
    public Page<UserDTO> getUsers(QueryUserRequest queryUserRequest) {
        Page<UserEntity> userPage = userDomainService.getUsers(queryUserRequest);

        // 转换为DTO
        List<UserDTO> userDTOList = userPage.getRecords().stream().map(UserAssembler::toDTO).toList();

        // 创建返回的分页对象
        Page<UserDTO> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        resultPage.setRecords(userDTOList);
        return resultPage;
    }
}
