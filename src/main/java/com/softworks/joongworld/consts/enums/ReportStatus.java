package com.softworks.joongworld.consts.enums;

import java.util.Arrays;

/**
 * 신고 처리 상태
 */
public enum ReportStatus {
    PENDING("대기"),
    IN_PROGRESS("처리중"),
    RESOLVED("처리완료");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReportStatus from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return PENDING;
        }
        String trimmed = value.trim();
        String upper = trimmed.toUpperCase();
        return Arrays.stream(values())
            .filter(status -> status.name().equals(upper) || status.displayName.equals(trimmed))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 신고 상태입니다: " + value));
    }
}
