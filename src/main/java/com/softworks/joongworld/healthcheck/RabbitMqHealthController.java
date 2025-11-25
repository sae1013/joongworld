package com.softworks.joongworld.healthcheck;

import com.softworks.joongworld.config.RabbitMqConfig;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitMqHealthController {

    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/health/rabbitmq")
    public ResponseEntity<Map<String, Object>> pingRabbit() {
        try {
            rabbitTemplate.execute(channel -> {
                channel.queueDeclarePassive(RabbitMqConfig.ADMIN_SIGNUP_QUEUE);
                return "";
            });

            String payload = "PING-" + Instant.now();
            rabbitTemplate.convertAndSend(RabbitMqConfig.ADMIN_SIGNUP_QUEUE, payload);

            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "queue", RabbitMqConfig.ADMIN_SIGNUP_QUEUE,
                    "payload", payload
            ));
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body(Map.of(
                            "status", "DOWN",
                            "error", e.getClass().getSimpleName(),
                            "message", e.getMessage()
                    ));
        }
    }
}
