package io.github.hoooosi.meeting.common.utils;

import io.github.hoooosi.meeting.common.constants.CacheNames;
import io.github.hoooosi.meeting.common.model.dto.TokenDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate;

    public String saveCheckCode(String code) {
        String key = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(CacheNames.CHECK_CODE + key, code, 60, TimeUnit.SECONDS);
        return key;
    }

    public String getCheckCode(String key) {
        Object obj = redisTemplate.opsForValue().get(CacheNames.CHECK_CODE + key);
        if (obj == null)
            return null;
        return (String) obj;
    }

    public void deleteCheckCode(String key) {
        redisTemplate.delete(CacheNames.CHECK_CODE + key);
    }

    public void setTokenDTO(TokenDTO tokenDTO) {
        String token = tokenDTO.getTokenKey();
        Object oldToken = redisTemplate.opsForValue().get(CacheNames.UID + tokenDTO.getUserId());
        if (oldToken != null) redisTemplate.unlink(CacheNames.TOKEN + oldToken);
        redisTemplate.opsForValue().set(CacheNames.TOKEN + token, tokenDTO);
        redisTemplate.opsForValue().set(CacheNames.UID + tokenDTO.getUserId(), token);
    }

    public void setRoomId(Long userId, Long roomId) {
        TokenDTO tokenDTO = getTokenDTO(userId);
        if (tokenDTO == null) return;
        tokenDTO.setRoomId(roomId);
        log.info("Setting roomId {} for userId {}, dto: {}", roomId, userId,tokenDTO);
        redisTemplate.opsForValue().set(CacheNames.TOKEN + tokenDTO.getTokenKey(), tokenDTO);
    }

    public TokenDTO getTokenDTO(String tokenKey) {
        Object obj = redisTemplate.opsForValue().get(CacheNames.TOKEN + tokenKey);
        if (obj == null) return null;
        return (TokenDTO) obj;
    }

    public TokenDTO getTokenDTO(Long userId) {
        Object tokenKey = redisTemplate.opsForValue().get(CacheNames.UID + userId);
        if (tokenKey == null) return null;
        Object obj = redisTemplate.opsForValue().get(CacheNames.TOKEN + tokenKey);
        if (obj == null) return null;
        return (TokenDTO) obj;
    }
}
