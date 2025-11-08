package com.softworks.joongworld.consts.enums;

import java.util.Arrays;

/**
 * 어드민 직책 구분 enum
 */
public enum AdminPosition {
    MANAGER("매니저"),
    SUPER_ADMIN("최고관리자");

    private final String displayName;

    AdminPosition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * DB 로부터 전달된 값을 enum 으로 변환한다.
     */
    public static AdminPosition from(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String upper = trimmed.toUpperCase();
        return Arrays.stream(values())
                .filter(position -> position.name().equals(upper) || position.displayName.equals(trimmed))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 직책입니다: " + value));
    }
}
