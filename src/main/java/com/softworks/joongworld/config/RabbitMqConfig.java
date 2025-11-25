package com.softworks.joongworld.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String ADMIN_SIGNUP_QUEUE = "admin.signup.requests";

    @Bean
    public Queue adminSignupQueue() {
        return QueueBuilder.durable(ADMIN_SIGNUP_QUEUE)
                .build();
    }
}
