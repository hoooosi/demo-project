package io.github.hoooosi.meeting.common.filter;

import io.github.hoooosi.meeting.common.context.UserContext;
import io.github.hoooosi.meeting.common.utils.RedisUtils;
import io.github.hoooosi.meeting.common.utils.TokenUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@AllArgsConstructor
@Slf4j
public class ThreadLocalCleanupFilter implements Filter {

    private final RedisUtils redisUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            parseAndSetContext(request);
            chain.doFilter(request, response);
        } finally {
            TokenUtils.clear();
        }
    }

    private void parseAndSetContext(ServletRequest request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return;
        }

        String token = httpRequest.getHeader("token");
        if (token == null || token.trim().isEmpty()) {
            TokenUtils.setUserContext(UserContext.empty());
            return;
        }

        token = token.trim();

        Long userId = redisUtils.getUserIdByToken(token);
        if (userId != null) {
            UserContext userContext = new UserContext(token, userId);
            TokenUtils.setUserContext(userContext);
        } else {
            TokenUtils.setUserContext(UserContext.empty());
        }
    }
}
