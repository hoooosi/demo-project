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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final RedisUtils redisUtils;
    private final UserService userService;
    private final TokenUtils tokenUtils;

    @GetMapping("/checkCode")
    public Resp<CheckCodeVO> checkCode() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String code = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        String key = redisUtils.saveCheckCode(code);
        return Resp.success(new CheckCodeVO(key, base64));
    }

    @PostMapping("/register")
    public Resp<Void> register(@RequestBody UserDTO.RegisterDTO dto) {
        String code = dto.checkCode().toLowerCase();
        String rightCode = redisUtils.getCheckCode(dto.checkCodeKey());
        ThrowUtils.throwIf(rightCode == null, ErrorCode.CHECK_CODE_EXPIRED);
        ThrowUtils.throwIf(!rightCode.equals(code), ErrorCode.CHECK_CODE_ERROR);
        redisUtils.deleteCheckCode(dto.checkCodeKey());
        userService.register(dto.nickName(), dto.password(), dto.email());
        return Resp.success();
    }

    @PostMapping("/login")
    public Resp<String> login(@RequestBody UserDTO.LoginDTO dto) {
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
        Long userId = tokenUtils.getUserId();
        User user = userService.getById(userId);
        return Resp.success(user);
    }

    @GetMapping("getApply")
    public Resp<Void> getApply() {
        return Resp.success(null);
    }
}
