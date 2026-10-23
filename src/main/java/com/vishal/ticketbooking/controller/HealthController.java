package com.vishal.ticketbooking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    // JdbcTemplate lets us run a simple SQL query directly. Spring supplies it.
    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ENDPOINT 7: GET /api/v1/health -> checks the app AND the PostgreSQL Docker database.
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        try {
            // "SELECT 1" is the lightest possible query. If it works, the DB is reachable.
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("status", "UP", "database", "UP"));
        } catch (Exception e) {
            // DB unreachable (e.g. Docker container stopped) -> HTTP 503 Service Unavailable.
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("status", "DOWN", "database", "DOWN"));
        }
    }
}