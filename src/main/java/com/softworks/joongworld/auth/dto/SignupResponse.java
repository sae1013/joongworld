package com.softworks.joongworld.auth.dto;

public record SignupResponse(
        Long userId,
        String email,
        String name,
        String nickname,
        boolean isAdmin
) {
}
