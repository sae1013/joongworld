package com.softworks.joongworld.auth.support;

import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserHelper {

    @ModelAttribute("currentUser")
    public LoginUserInfo currentUser(HttpServletRequest request) {
        Object attr = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        if (attr instanceof LoginUserInfo info) {
            return info;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info) {
            return info;
        }
        return LoginUserInfo.empty();
    }
}
