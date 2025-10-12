package io.github.hoooosi.meeting.controller;

import com.wf.captcha.SpecCaptcha;
import io.github.hoooosi.meeting.common.annotation.CheckLogin;
import io.github.hoooosi.meeting.common.model.dto.UserDTO;
import io.github.hoooosi.meeting.common.model.entity.User;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.service.UserService;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.ThrowUtils;
import io.github.hoooosi.meeting.common.utils.TokenUtils;
import io.github.hoooosi.meeting.common.model.vo.CheckCodeVO;
import io.github.hoooosi.meeting.common.model.vo.Resp;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final RedisUtils redisUtils;
    private final UserService userService;

    @GetMapping("/checkCode")
    public Resp<CheckCodeVO> checkCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String code = captcha.text();
        String base64 = captcha.toBase64();
        String key = redisUtils.saveCheckCode(code);
        return Resp.success(new CheckCodeVO(key, base64));
    }

    @PostMapping("/register")
    public Resp<Void> register(UserDTO.RegisterDTO dto) {
        String rightCode = redisUtils.getCheckCode(dto.checkCodeKey());
        ThrowUtils.throwIf(rightCode == null, ErrorCode.CHECK_CODE_EXPIRED);
        ThrowUtils.throwIf(!rightCode.equals(dto.checkCode()), ErrorCode.CHECK_CODE_ERROR);
        userService.register(dto.nickName(), dto.password(), dto.email());
        return Resp.success();
    }

    @PostMapping("/login")
    public Resp<String> login(UserDTO.LoginDTO dto) {
        String token = userService.login(dto.email(), dto.password());
        return Resp.success(token);
    }

    @PostMapping("/logout")
    @CheckLogin
    public Resp<Void> logout() {
        return Resp.success();
    }

    @CheckLogin
    @GetMapping("/me")
    public Resp<User> getUserInfo() {
        Long userId = TokenUtils.getUserId();
        User user = userService.getById(userId);
        return Resp.success(user);
    }

    @CheckLogin
    @PutMapping()
    public Resp<Void> updateNickname() {
        return Resp.success();
    }
}
