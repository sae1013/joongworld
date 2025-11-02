package com.softworks.joongworld.auth.support;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class SessionCookieUtils {

    public static final String SESSION_COOKIE = "SESSION";

    private SessionCookieUtils() {
    }

    public static ResponseCookie createSessionCookie(String token, boolean secure, Duration ttl) {
        Duration effectiveTtl = (ttl != null && !ttl.isNegative() && !ttl.isZero())
                ? ttl
                : Duration.ofHours(3);
        return baseBuilder(token, secure)
                .maxAge(effectiveTtl)
                .build();
    }

    public static ResponseCookie deleteSessionCookie(boolean secure) {
        return baseBuilder("", secure)
                .maxAge(Duration.ZERO)
                .build();
    }

    private static ResponseCookie.ResponseCookieBuilder baseBuilder(String token, boolean secure) {
        return ResponseCookie.from(SESSION_COOKIE, token != null ? token : "")
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax");
    }
}
