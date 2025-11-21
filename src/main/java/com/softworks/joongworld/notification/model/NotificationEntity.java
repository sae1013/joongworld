package com.softworks.joongworld.notification.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class NotificationEntity {
    private Long id;
    private NotificationType type;
    private Long recipientId;
    private Long actorId;
    private NotificationTargetType targetType;
    private Long targetId;
    private String message;
    private String metadataJson;
    private NotificationStatus status;
    private OffsetDateTime readAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
