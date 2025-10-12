package io.github.hoooosi.meeting.common.context;

/**
 * 用户上下文信息
 * 保存当前请求的用户相关信息
 *
 * @param token  用户令牌
 * @param userId 用户ID
 */
public record UserContext(String token, Long userId) {

    public static UserContext empty() {
        return new UserContext(null, null);
    }

    public boolean isLogin() {
        return userId != null;
    }

    public boolean isNotLogin() {
        return userId == null;
    }
}

