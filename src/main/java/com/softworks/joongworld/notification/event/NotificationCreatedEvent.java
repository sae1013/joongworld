package com.softworks.joongworld.notification.event;

import com.softworks.joongworld.notification.dto.NotificationView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {
    private final NotificationView notification;
    private final int unreadCount;
}
