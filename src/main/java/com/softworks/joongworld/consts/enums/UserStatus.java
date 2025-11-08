package com.softworks.joongworld.consts.enums;

import java.util.Arrays;

/**
 * 사용자 상태
 */
public enum UserStatus {
    ACTIVE("활성"),
    DORMANT("휴면"),
    SUSPENDED("정지"),
    WITHDRAWN("탈퇴");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserStatus from(String value) {
        if (value == null) {
            return ACTIVE;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return ACTIVE;
        }

        String upper = trimmed.toUpperCase();
        return Arrays.stream(values())
                .filter(status -> status.name().equals(upper) || status.displayName.equals(trimmed))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 상태입니다: " + value));
    }
}
