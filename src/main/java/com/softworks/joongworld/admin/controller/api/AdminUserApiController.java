package com.softworks.joongworld.admin.controller.api;

import com.softworks.joongworld.admin.service.AdminUserService;
import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminUserApiController {

    private final AdminUserService adminUserService;

    @PostMapping("/api/admin/users/{userId}/approve")
    public ResponseEntity<Void> approve(@PathVariable("userId") Long userId,
                                        @CurrentUser LoginUserInfo currentUser) {
        adminUserService.approvePendingManager(userId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/admin/users/{userId}/reject")
    public ResponseEntity<Void> reject(@PathVariable("userId") Long userId,
                                       @CurrentUser LoginUserInfo currentUser) {
        adminUserService.rejectPendingManager(userId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
