package com.softworks.joongworld.healthcheck;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class RedisHealthController {

    private final RedisConnectionFactory connectionFactory;

    @GetMapping("/redis")
    public ResponseEntity<Map<String, Object>> checkRedis() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            String ping = connection.ping();
            boolean ok = "PONG".equalsIgnoreCase(ping);
            String version = null;
            var info = connection.serverCommands().info("server");
            if (info != null) {
                version = info.getProperty("redis_version");
            }
            return ResponseEntity.ok(Map.of(
                    "status", ok ? "UP" : "DOWN",
                    "redisVersion", version
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body(Map.of("status", "DOWN", "error", e.getClass().getSimpleName()));
        }
    }
}
