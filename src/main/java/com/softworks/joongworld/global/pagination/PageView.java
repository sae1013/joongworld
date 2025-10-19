package com.softworks.joongworld.global.pagination;

import java.util.List;
import java.util.Objects;

public record PageView<T>(
        List<T> items,
        int page,
        int size,
        long totalCount,
        int totalPages,
        boolean hasPrevious,
        boolean hasNext
) {

    /**
     *
     * @param items 리스트
     * @param page 페이지
     * @param size 한 페이지당 아이템 갯수
     * @param totalCount 전체 갯수
     * @param totalPages 전체 페이지 수
     * @param hasPrevious 이전버튼
     * @param hasNext 다음버튼
     */
    public PageView {
        Objects.requireNonNull(items, "no item");
        if (page < 1) {
            throw new IllegalArgumentException("page must be >= 1");
        }
        if (size < 1) {
            throw new IllegalArgumentException("page size must be >= 1");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount must be >= 0");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages must be >= 0");
        }
    }
}
