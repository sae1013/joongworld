package com.softworks.joongworld.notification.dto;

import com.softworks.joongworld.notification.model.NotificationTargetType;
import com.softworks.joongworld.notification.model.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class NotificationCreateCommand {
    private final NotificationType type;
    private final Long recipientId;
    private final Long actorId;
    private final NotificationTargetType targetType;
    private final Long targetId;
    private final String message;
    @Builder.Default
    private final Map<String, Object> metadata = Collections.emptyMap();
}
