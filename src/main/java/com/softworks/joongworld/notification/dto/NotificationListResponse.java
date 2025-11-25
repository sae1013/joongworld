package com.softworks.joongworld.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class NotificationListResponse {
    @Builder.Default
    private final int unreadCount = 0;
    @Builder.Default
    private final List<NotificationView> notifications = Collections.emptyList();

    public static NotificationListResponse of(int unreadCount, List<NotificationView> notifications) {
        return NotificationListResponse.builder()
                .unreadCount(Math.max(unreadCount, 0))
                .notifications(notifications == null ? Collections.emptyList() : notifications)
                .build();
    }
}
