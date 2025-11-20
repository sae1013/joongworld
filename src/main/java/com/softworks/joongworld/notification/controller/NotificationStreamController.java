package com.softworks.joongworld.notification.controller;

import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.notification.dto.NotificationListResponse;
import com.softworks.joongworld.notification.dto.NotificationView;
import com.softworks.joongworld.notification.service.NotificationService;
import com.softworks.joongworld.notification.support.NotificationSseService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationStreamController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    @GetMapping("/stream")
    public SseEmitter stream(@CurrentUser LoginUserInfo currentUser) {
        Long userId = ensureLogin(currentUser);
        SseEmitter emitter = notificationSseService.subscribe(userId);
        List<NotificationView> notifications = notificationService.getRecent(userId, null);
        int unreadCount = notificationService.countUnread(userId);
        notificationSseService.sendInitial(emitter, NotificationListResponse.of(unreadCount, notifications));
        return emitter;
    }

    private Long ensureLogin(LoginUserInfo currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return currentUser.getId();
    }
}
