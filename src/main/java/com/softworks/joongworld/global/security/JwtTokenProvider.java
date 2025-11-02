package com.softworks.joongworld.global.security;

import com.softworks.joongworld.user.dto.UserAuth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenTtl;
    private final long refreshTokenTtl;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secretValue,
                            @Value("${app.jwt.access-token-expiration-ms:10800000}") long accessTokenTtl,
                            @Value("${app.jwt.refresh-token-expiration-ms:86400000}") long refreshTokenTtl) {
        String secret = secretValue != null ? secretValue.trim() : "";
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String createAccessToken(UserAuth user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenTtl);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiry)
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("admin", user.isAdmin())
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(UserAuth user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenTtl);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(expiry)
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }
}
