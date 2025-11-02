package com.softworks.joongworld.auth.controller;

import com.softworks.joongworld.auth.dto.LoginRequest;
import com.softworks.joongworld.auth.dto.SignupRequest;
import com.softworks.joongworld.auth.dto.SignupResponse;
import com.softworks.joongworld.auth.service.AuthService;
import com.softworks.joongworld.auth.service.SessionService;
import com.softworks.joongworld.auth.service.SignupService;
import com.softworks.joongworld.auth.support.SessionCookieUtils;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApiController {

    private final SignupService signupService;
    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = signupService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserInfo> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest,
                                               HttpServletResponse httpResponse) {
        LoginUserInfo user = authService.login(request);

        boolean secure = httpRequest.isSecure();
        Duration sessionTtl = sessionService.getDefaultTtl();
        String sessionToken = sessionService.createSession(user, sessionTtl);

        ResponseCookie sessionCookie = SessionCookieUtils.createSessionCookie(sessionToken, secure, sessionTtl);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        String token = extractSessionToken(httpRequest);
        if (token != null) {
            sessionService.deleteSession(token);
        }
        boolean secure = httpRequest.isSecure();
        ResponseCookie deleteCookie = SessionCookieUtils.deleteSessionCookie(secure);
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.noContent().build();
    }

    private String extractSessionToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> SessionCookieUtils.SESSION_COOKIE.equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
