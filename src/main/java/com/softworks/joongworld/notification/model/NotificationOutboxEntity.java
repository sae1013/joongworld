package com.softworks.joongworld.notification.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class NotificationOutboxEntity {
    private Long id;
    private Long notificationId;
    private String eventType;
    private String payload;
    private OffsetDateTime publishedAt;
    private OffsetDateTime deliveredAt;
    private String errorMessage;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
