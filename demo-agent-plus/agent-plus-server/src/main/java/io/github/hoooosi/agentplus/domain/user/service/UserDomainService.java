package io.github.hoooosi.agentplus.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.hoooosi.agentplus.domain.user.service.model.UserEntity;
import io.github.hoooosi.agentplus.domain.user.service.model.UserSettingsEntity;
import io.github.hoooosi.agentplus.domain.user.service.repository.UserRepository;
import io.github.hoooosi.agentplus.domain.user.service.repository.UserSettingsRepository;
import io.github.hoooosi.agentplus.infrastructure.utils.PasswordUtils;
import io.github.hoooosi.agentplus.interfaces.dto.users.request.QueryUserRequest;
import io.github.hoooosi.agentplus.infrastructure.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final UserSettingsRepository settingsRepository;

    /** 获取用户信息 */
    public UserEntity getUserInfo(Long id) {
        return userRepository.selectById(id);
    }

    /** 根据邮箱或手机号查找用户
     * @param account 邮箱或手机号
     * @return 用户实体，如果不存在则返回null */
    public UserEntity findUserByAccount(String account) {
        LambdaQueryWrapper<UserEntity> wrapper = Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getEmail, account)
                .or().eq(UserEntity::getPhone, account);
        return userRepository.selectOne(wrapper);
    }

    /** 根据GitHub ID查找用户
     * @param githubId GitHub ID
     * @return 用户实体，如果不存在则返回null */
    public UserEntity findUserByGithubId(String githubId) {
        LambdaQueryWrapper<UserEntity> wrapper = Wrappers.<UserEntity>lambdaQuery()
                .eq(UserEntity::getGithubId, githubId);
        return userRepository.selectOne(wrapper);
    }

    /** 注册 密码加密存储
     * @param email 邮箱
     * @param phone 手机号
     * @param password 明文密码
     * @return 用户实体 */
    public UserEntity register(String email, String phone, String password) {
        UserEntity userEntity = new UserEntity()
                .setEmail(email)
                .setPhone(phone)
                .setPassword(PasswordUtils.encode(password));
        userEntity.valid();
        checkAccountExist(userEntity.getEmail());

        // 生成昵称
        String nickname = generateNickname();
        userEntity.setNickname(nickname);

        // 设置普通登录平台
        userEntity.setLoginPlatform("normal");

        //userRepository.checkInsert(userEntity);
        UserSettingsEntity userSettingsEntity = new UserSettingsEntity();
        userSettingsEntity.setUserId(userEntity.getId());
        // settingsRepository.insert(userSettingsEntity);
        return userEntity;
    }

    /** 检查账号是否存在，邮箱 or 手机号任意值
     * @param email 邮箱账号 */
    public void checkAccountExist(String email) {
        LambdaQueryWrapper<UserEntity> wrapper = Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getEmail, email)
                .or();
        if (userRepository.exists(wrapper)) {
            throw new BusinessException("账号已存在,不可重复账注册");
        }
    }

    /** 用户登录 验证账号密码
     * @param account 邮箱或手机号
     * @param password 明文密码
     * @return 用户实体 */
    public UserEntity login(String account, String password) {
        LambdaQueryWrapper<UserEntity> wrapper = Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getEmail, account)
                .or().eq(UserEntity::getPhone, account);

        UserEntity userEntity = userRepository.selectOne(wrapper);

        if (userEntity == null || !PasswordUtils.matches(password, userEntity.getPassword())) {
            throw new BusinessException("账号密码错误");
        }
        return userEntity;
    }

    /** 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码 */
    public void updatePassword(Long userId, String newPassword) {
        UserEntity user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 加密新密码
        String encodedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.checkedUpdateById(user);
    }

    /** 修改用户密码（需要验证当前密码）
     * @param userId 用户ID
     * @param currentPassword 当前密码
     * @param newPassword 新密码 */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        UserEntity user = userRepository.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证当前密码
        if (!PasswordUtils.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("当前密码不正确");
        }

        // 检查新密码是否与当前密码相同
        if (PasswordUtils.matches(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与当前密码相同");
        }

        // 加密新密码并更新
        String encodedPassword = PasswordUtils.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.checkedUpdateById(user);
    }

    /** 根据用户ID列表批量获取用户信息
     * @param userIds 用户ID列表
     * @return 用户实体列表 */
    public List<UserEntity> getByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return userRepository.selectByIds(userIds);
    }

    /** 创建默认用户（用于系统初始化） 绕过常规的业务校验，直接插入用户数据
     * @param user 用户实体 */
    public void createDefaultUser(UserEntity user) {
        // 设置基础字段
        user.valid();

        // 直接插入，不进行重复性校验（因为调用方已经检查过）
        userRepository.insert(user);

        // 创建用户设置
        UserSettingsEntity userSettingsEntity = new UserSettingsEntity();
        userSettingsEntity.setUserId(user.getId());
        settingsRepository.insert(userSettingsEntity);
    }

    /** 分页查询用户列表
     *
     * @param queryUserRequest 查询条件
     * @return 用户分页数据 */
    public Page<UserEntity> getUsers(QueryUserRequest queryUserRequest) {
        LambdaQueryWrapper<UserEntity> wrapper = Wrappers.<UserEntity>lambdaQuery();

        // 关键词搜索：昵称、邮箱、手机号
        if (queryUserRequest.getKeyword() != null && !queryUserRequest.getKeyword().trim().isEmpty()) {
            String keyword = queryUserRequest.getKeyword().trim();
            wrapper.and(w -> w.like(UserEntity::getNickname, keyword).or().like(UserEntity::getEmail, keyword).or()
                    .like(UserEntity::getPhone, keyword));
        }

        // 按创建时间倒序排列
        wrapper.orderByDesc(UserEntity::getCreatedAt);

        // 分页查询
        Page<UserEntity> page = new Page<>(queryUserRequest.getPage(), queryUserRequest.getPageSize());
        return userRepository.selectPage(page, wrapper);
    }

    /** 随机生成用户昵称
     * @return 用户昵称 */
    private String generateNickname() {
        return "agent-plus" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public void updateUserInfo(UserEntity user) {
        userRepository.checkedUpdateById(user);
    }
}
