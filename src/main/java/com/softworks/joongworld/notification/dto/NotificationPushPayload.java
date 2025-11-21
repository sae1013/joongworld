package com.softworks.joongworld.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationPushPayload {
    private final NotificationView notification;
    private final int unreadCount;
}
