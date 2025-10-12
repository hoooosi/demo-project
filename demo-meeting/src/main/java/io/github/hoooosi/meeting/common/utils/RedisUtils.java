package io.github.hoooosi.meeting.common.utils;

import io.github.hoooosi.meeting.common.constants.CacheNames;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate;

    public String saveCheckCode(String code) {
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(CacheNames.CHECK_CODE + key, code);
        return key;
    }

    public String getCheckCode(String key) {
        Object obj = redisTemplate.opsForValue().get(CacheNames.CHECK_CODE + key);
        if (obj == null)
            return null;
        return (String) obj;
    }

    public String loginAndGetToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(CacheNames.LOGIN_TOKEN + token, userId);
        return token;
    }

    public Long getUserIdByToken(String token) {
        Object obj = redisTemplate.opsForValue().get(CacheNames.LOGIN_TOKEN + token);
        if (obj == null)
            return null;
        return (Long) obj;
    }
}
