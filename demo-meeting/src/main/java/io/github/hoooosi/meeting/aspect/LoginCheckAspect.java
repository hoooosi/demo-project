package io.github.hoooosi.meeting.aspect;

import io.github.hoooosi.meeting.common.annotation.CheckLogin;
import io.github.hoooosi.meeting.common.exception.BusinessException;
import io.github.hoooosi.meeting.common.exception.ErrorCode;
import io.github.hoooosi.meeting.common.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 登录校验切面
 * 拦截带有 @CheckLogin 注解的方法，验证用户是否已登录
 * 用户上下文由 ThreadLocalCleanupFilter 在请求开始时设置
 */
@Aspect
@Component
@Slf4j
public class LoginCheckAspect {

    @Around("@annotation(checkLogin)")
    public Object checkLogin(ProceedingJoinPoint joinPoint, CheckLogin checkLogin) throws Throwable {
        // 检查用户是否已登录
        if (TokenUtils.isNotLogin()) {
            log.warn("用户未登录或 token 无效，拒绝访问: {}", joinPoint.getSignature());
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = TokenUtils.getUserContext().userId();
        log.debug("用户 {} 通过鉴权，访问: {}", userId, joinPoint.getSignature());

        // 继续执行目标方法
        return joinPoint.proceed();
    }
}
