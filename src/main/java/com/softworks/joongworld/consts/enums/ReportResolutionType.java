package com.softworks.joongworld.consts.enums;

import java.util.Arrays;

/**
 * 신고 처리 결과 유형
 */
public enum ReportResolutionType {
    NO_ACTION("조치 없음"),
    WARNED("경고 조치"),
    SUSPENDED_USER("계정 정지"),
    BLOCKED_PRODUCT("상품 차단"),
    OTHER("기타");

    private final String displayName;

    ReportResolutionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReportResolutionType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String trimmed = value.trim();
        String upper = trimmed.toUpperCase();
        return Arrays.stream(values())
            .filter(resolution -> resolution.name().equals(upper) || resolution.displayName.equals(trimmed))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 신고 처리 결과입니다: " + value));
    }
}
