package com.softworks.joongworld.notification.controller;

import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.notification.dto.NotificationListResponse;
import com.softworks.joongworld.notification.dto.NotificationView;
import com.softworks.joongworld.notification.service.NotificationService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(@CurrentUser LoginUserInfo currentUser,
                                                                     @RequestParam(required = false) Integer limit) {
        Long userId = ensureLogin(currentUser);
        List<NotificationView> notifications = notificationService.getRecent(userId, limit);
        int unreadCount = notificationService.countUnread(userId);
        return ResponseEntity.ok(NotificationListResponse.of(unreadCount, notifications));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
                                           @CurrentUser LoginUserInfo currentUser) {
        Long userId = ensureLogin(currentUser);
        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@CurrentUser LoginUserInfo currentUser) {
        Long userId = ensureLogin(currentUser);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    private Long ensureLogin(LoginUserInfo currentUser) {
        if (currentUser == null || currentUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return currentUser.getId();
    }
}
