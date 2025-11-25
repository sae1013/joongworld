package com.softworks.joongworld.consts.enums;

/**
 * 신고 대상 구분
 */
public enum ReportTargetType {
    USER,
    PRODUCT;

    public static ReportTargetType from(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("신고 대상을 선택해 주세요.");
        }
        return ReportTargetType.valueOf(value.trim().toUpperCase());
    }
}
