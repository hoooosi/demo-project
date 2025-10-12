package io.github.hoooosi.meeting.common.utils;

import io.github.hoooosi.meeting.common.context.UserContext;
import io.github.hoooosi.meeting.common.exception.BusinessException;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenUtils {

    /**
     * 线程局部变量，存储当前线程的用户上下文
     */
    private static final ThreadLocal<UserContext> USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程的用户上下文
     *
     * @param userContext 用户上下文
     */
    public static void setUserContext(UserContext userContext) {
        USER_CONTEXT_THREAD_LOCAL.set(userContext);
    }

    /**
     * 获取当前线程的用户上下文
     *
     * @return 用户上下文，如果未设置返回空上下文
     */
    public static UserContext getUserContext() {
        UserContext context = USER_CONTEXT_THREAD_LOCAL.get();
        return context != null ? context : UserContext.empty();
    }

    /**
     * 获取当前登录用户的 token
     *
     * @return token，如果未登录返回 null
     */
    public static String getToken() {
        return getUserContext().token();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID，如果未登录返回 null
     */
    public static Long getUserId() {
        return getUserContext().userId();
    }

    /**
     * 获取当前登录用户ID（强制要求已登录）
     * 如果未登录则抛出异常
     *
     * @return 用户ID
     * @throws BusinessException 未登录时抛出
     */
    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            log.warn("尝试获取用户ID但用户未登录");
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return userId;
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isLogin() {
        return getUserContext().isLogin();
    }

    /**
     * 检查当前用户是否未登录
     *
     * @return true-未登录，false-已登录
     */
    public static boolean isNotLogin() {
        return getUserContext().isNotLogin();
    }

    /**
     * 要求用户必须已登录，否则抛出异常
     *
     * @throws BusinessException 未登录时抛出
     */
    public static void requireLogin() {
        if (isNotLogin()) {
            log.warn("访问受保护资源但用户未登录");
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * 要求用户必须未登录，否则抛出异常（用于注册、登录等接口）
     *
     * @throws BusinessException 已登录时抛出
     */
    public static void requireNotLogin() {
        if (isLogin()) {
            log.warn("访问仅限未登录用户的资源但用户已登录");
            throw new BusinessException(ErrorCode.ALREADY_LOGIN);
        }
    }

    /**
     * 检查当前用户是否为指定用户
     *
     * @param userId 要检查的用户ID
     * @return true-是当前用户，false-不是当前用户
     */
    public static boolean isCurrentUser(Long userId) {
        if (userId == null) {
            return false;
        }
        Long currentUserId = getUserId();
        return userId.equals(currentUserId);
    }

    /**
     * 要求当前用户必须是指定用户，否则抛出异常（用于权限校验）
     *
     * @param userId 要求的用户ID
     * @throws BusinessException 不是指定用户时抛出
     */
    public static void requireCurrentUser(Long userId) {
        if (!isCurrentUser(userId)) {
            log.warn("权限不足：当前用户 {} 尝试访问用户 {} 的资源", getUserId(), userId);
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 清除当前线程的用户上下文
     * 注意：每次请求结束后应该调用此方法，避免内存泄漏
     */
    public static void clear() {
        USER_CONTEXT_THREAD_LOCAL.remove();
    }
}
