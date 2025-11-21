package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminSignupRequest;
import com.softworks.joongworld.admin.dto.AdminSignupResponse;
import com.softworks.joongworld.auth.dto.SignupResponse;
import com.softworks.joongworld.auth.service.SignupService;
import com.softworks.joongworld.consts.enums.AdminPosition;
import com.softworks.joongworld.notification.dto.NotificationCreateCommand;
import com.softworks.joongworld.notification.model.NotificationTargetType;
import com.softworks.joongworld.notification.model.NotificationType;
import com.softworks.joongworld.notification.service.NotificationService;
import com.softworks.joongworld.user.repository.UserMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SignupService signupService;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Transactional
    public AdminSignupResponse registerAdmin(AdminSignupRequest request) {
        SignupResponse response = signupService.register(request);
        AdminSignupResponse adminResponse = AdminSignupResponse.from(response);
        notifySuperAdmins(request, adminResponse);
        return adminResponse;
    }

    private void notifySuperAdmins(AdminSignupRequest request, AdminSignupResponse response) {
        if (request == null || response == null) {
            return;
        }
        AdminPosition position = request.getPosition();
        if (position != AdminPosition.MANAGER) {
            return;
        }
        List<Long> superAdmins = userMapper.findAdminIdsByPosition(AdminPosition.SUPER_ADMIN.getDisplayName());
        if (CollectionUtils.isEmpty(superAdmins)) {
            return;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("managerId", response.getUserId());
        metadata.put("email", response.getEmail());
        metadata.put("nickname", response.getNickname());
        metadata.put("link", "/admin/users");

        for (Long recipientId : superAdmins) {
            if (recipientId == null) {
                continue;
            }
            notificationService.create(NotificationCreateCommand.builder()
                .type(NotificationType.ADMIN_MANAGER_SIGNUP)
                .recipientId(recipientId)
                .actorId(response.getUserId())
                .targetType(NotificationTargetType.USER)
                .targetId(response.getUserId())
                .message(buildManagerSignupMessage(response))
                .metadata(metadata)
                .build());
        }
    }

    private String buildManagerSignupMessage(AdminSignupResponse response) {
        String nickname = response.getNickname() != null ? response.getNickname() : "새 매니저";
        return nickname + " 매니저가 가입을 완료했습니다.";
    }
}
