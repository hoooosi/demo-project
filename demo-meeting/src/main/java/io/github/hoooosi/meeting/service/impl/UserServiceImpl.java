package io.github.hoooosi.meeting.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hoooosi.meeting.common.model.entity.User;
import io.github.hoooosi.meeting.common.enums.UserStatusEnum;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.mapper.UserMapper;
import io.github.hoooosi.meeting.service.UserService;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RedisUtils redisUtils;

    @Override
    public void register(String nickname, String password, String email) {
        // check existing email
        ThrowUtils.throwIf(this.exists(Wrappers.lambdaQuery(User.class).eq(User::getEmail, email)), ErrorCode.EMAIL_EXISTS);

        // create user entity
        User user = new User()
                .setEmail(email)
                .setNickName(nickname)
                .setPassword(DigestUtil.md5Hex(password))
                .setSex(0)
                .setStatus(UserStatusEnum.ENABLE.getValue());

        ThrowUtils.throwIf(this.save(user), ErrorCode.CHECK_CODE_ERROR);
    }

    @Override
    public String login(String email, String password) {
        User entity = this.getOne(Wrappers.lambdaQuery(User.class).eq(User::getEmail, email));

        ThrowUtils.throwIf(entity == null, ErrorCode.USER_NOT_FOUND);
        ThrowUtils.throwIf(UserStatusEnum.DISABLE.getValue().equals(entity.getStatus()), ErrorCode.USER_FORBIDDEN);
        ThrowUtils.throwIf(!DigestUtil.md5Hex(password).equals(entity.getPassword()), ErrorCode.PASSWORD_ERROR);

        String token = redisUtils.loginAndGetToken(entity.getUserId());
        entity.setLastLoginTime(System.currentTimeMillis());
        userMapper.updateById(entity);

        return token;
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

