package com.softworks.joongworld.user.dto;

import java.time.OffsetDateTime;

public record UserResponse(
        Long id,
        String email,
        String name,
        String nickname,
        boolean isAdmin,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
