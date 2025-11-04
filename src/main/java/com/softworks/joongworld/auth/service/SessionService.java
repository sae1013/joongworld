package com.softworks.joongworld.auth.service;

import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private static final String SESSION_PREFIX = "session:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(3);

    private final RedisTemplate<String, Object> redisTemplate;

    public String createSession(LoginUserInfo userInfo, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Duration effectiveTtl = normalizeTtl(ttl);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        // Redis 에 {session:토큰} 형태로 사용자 정보를 TTL 과 함께 저장한다.
        ops.set(key(token), userInfo, effectiveTtl);
        return token;
    }

    public LoginUserInfo findSession(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(key(token));

        if (value instanceof LoginUserInfo loginUserInfo) {
            // Redis 에 캐시된 사용자 정보를 그대로 복원한다.
            return loginUserInfo;
        }
        return null;
    }

    public void deleteSession(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        redisTemplate.delete(key(token));
    }

    public Duration getDefaultTtl() {
        return DEFAULT_TTL;
    }

    private Duration normalizeTtl(Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return DEFAULT_TTL;
        }
        return ttl;
    }

    private String key(String token) {
        return SESSION_PREFIX + token;
    }
}
