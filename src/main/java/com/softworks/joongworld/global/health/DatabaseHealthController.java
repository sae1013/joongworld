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

    /**
     * DB Health Check API
     * @return
     */
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

    /**
     * DB posts 테이블 데이터 조회를 위한 dummy API
     * @return
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> inspectPosts() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from post", Integer.class);
        var samples = jdbcTemplate.query(
                "select id, title, price, region, created_at from post order by id limit 5",
                (rs, rowNum) -> Map.of(
                        "id", rs.getLong("id"),
                        "title", rs.getString("title"),
                        "price", rs.getInt("price"),
                        "region", rs.getString("region"),
                        "createdAt", rs.getTimestamp("created_at").toInstant().toString()
                )
        );
        return ResponseEntity.ok(Map.of(
                "count", count,
                "samples", samples
        ));
    }
}
