package com.softworks.joongworld.notification.support;

import com.softworks.joongworld.notification.dto.NotificationPushPayload;
import com.softworks.joongworld.notification.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationSseService notificationSseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        if (event == null || event.getNotification() == null) {
            return;
        }
        Long recipientId = event.getNotification().getRecipientId();
        if (recipientId == null) {
            return;
        }
        notificationSseService.sendNotification(recipientId, NotificationPushPayload.builder()
                .notification(event.getNotification())
                .unreadCount(event.getUnreadCount())
                .build());
    }
}
