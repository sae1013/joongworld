package com.softworks.joongworld.user.dto;

import com.softworks.joongworld.consts.enums.UserStatus;
import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String phoneNum,
        String position,
        boolean isAdmin,
        UserStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
