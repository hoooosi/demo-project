package io.github.hoooosi.meeting.common.utils;

import io.github.hoooosi.meeting.common.model.dto.TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@AllArgsConstructor
@Component
public class TokenUtils {
    private final RedisUtils redisUtils;

    private static final ThreadLocal<TokenDTO> USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public TokenDTO getTokenDTO() {
        TokenDTO tokenDTO = USER_CONTEXT_THREAD_LOCAL.get();
        if (tokenDTO == null) {
            tokenDTO = redisUtils.getTokenDTO(getToken());
            USER_CONTEXT_THREAD_LOCAL.set(tokenDTO);
        }
        return tokenDTO;
    }

    public Long getUserId() {
        TokenDTO tokenDTO = getTokenDTO();
        if (tokenDTO == null) {
            return null;
        }
        return tokenDTO.getUserId();
    }

    public boolean isLogin() {
        TokenDTO tokenDTO = getTokenDTO();
        return tokenDTO != null;
    }

    public static void clear() {
        USER_CONTEXT_THREAD_LOCAL.remove();
    }

    public static String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        String token = request.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        token = token.trim();
        return token;
    }

}
