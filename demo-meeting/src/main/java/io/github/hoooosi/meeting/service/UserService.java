package io.github.hoooosi.meeting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.hoooosi.meeting.common.model.entity.User;

public interface UserService extends IService<User> {
    void register(String nickname, String password, String email);

    String login(String email, String password);
}
