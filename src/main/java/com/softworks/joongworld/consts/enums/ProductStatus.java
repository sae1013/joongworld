package com.softworks.joongworld.consts.enums;

import java.util.Arrays;

/**
 * 상품 상태
 */
public enum ProductStatus {
    ACTIVE("활성"),
    HIDDEN("숨김"),
    BLOCKED("차단");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ProductStatus from(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ACTIVE;
        }
        String trimmed = value.trim();
        String upper = trimmed.toUpperCase();
        return Arrays.stream(values())
            .filter(status -> status.name().equals(upper) || status.displayName.equals(trimmed))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 상품 상태입니다: " + value));
    }
}
