package com.softworks.joongworld.global.health;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class DatabaseHealthController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> checkDb() {
        try {
            Integer one = jdbcTemplate.queryForObject("select 1", Integer.class);
            String ver = jdbcTemplate.queryForObject("select version()", String.class);
            return ResponseEntity.ok(Map.of("status", (one != null && one == 1) ? "UP" : "DOWN",
                    "databaseVersion", ver));
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body(Map.of("status", "DOWN", "error", e.getClass().getSimpleName()));
        }
    }
}
