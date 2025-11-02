package com.softworks.joongworld.auth.support;

import com.softworks.joongworld.auth.service.SessionService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    public static final String CURRENT_USER_ATTR = SessionAuthenticationFilter.class.getName() + ".CURRENT_USER";

    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LoginUserInfo currentUser = LoginUserInfo.empty();
        var existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.getPrincipal() instanceof LoginUserInfo info) {
            currentUser = info;
        } else {
            String token = extractSessionToken(request.getCookies());
            if (StringUtils.hasText(token)) {
                LoginUserInfo user = sessionService.findSession(token);
                if (user != null) {
                    log.debug("[auth] Redis Session user Email {}", user.getEmail());
                    setAuthentication(user);
                    currentUser = user;
                } else {
                    log.debug("[auth] Invalid session token {}", token);
                    sessionService.deleteSession(token);
                }
            }
        }
        request.setAttribute(CURRENT_USER_ATTR, currentUser);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(LoginUserInfo user) {
        List<SimpleGrantedAuthority> authorities = user.isAdmin()
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.emptyList();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractSessionToken(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (SessionCookieUtils.SESSION_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
