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

    /**
     * OncePerRequestFilter 라이브러리에서 강제하는 오버라이딩 메서드
     * 매 요청마다 스프링시큐리티를 사용한 인증절차에 사용
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LoginUserInfo currentUser = LoginUserInfo.empty();
        // 이미 인증 정보가 있다면 Redis 조회를 건너뛰고 그대로 사용한다.
        var existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.getPrincipal() instanceof LoginUserInfo info) {
            currentUser = info;
        } else {
            // 세션 쿠키에서 토큰을 꺼내 Redis 에서 사용자 정보를 복원한다.
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

    /**
     * 사용자 정보를 Spring Security 인증객체를 만듬. admin 여부에 따라 권한이 달라짐.
     * hasRole 을 사용
     * @param user
     */
    private void setAuthentication(LoginUserInfo user) {
        List<SimpleGrantedAuthority> authorities = user.isAdmin()
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.emptyList();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 요청에서 쿠키를 파싱하여 세션을 추출함
     * @param cookies
     * @return
     */
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
