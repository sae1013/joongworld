package com.softworks.joongworld.notification.support;

import com.softworks.joongworld.auth.support.SessionAuthenticationFilter;
import com.softworks.joongworld.notification.service.NotificationService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class NotificationCountInterceptor implements HandlerInterceptor {

    private final NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Object attr = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        if (attr instanceof LoginUserInfo user && user.getId() != null) {
            int count = notificationService.countUnread(user.getId());
            request.setAttribute("notificationCount", count);
        }
        return true;
    }
}
