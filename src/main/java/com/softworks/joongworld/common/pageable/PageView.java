package com.softworks.joongworld.common.pageable;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class PageView<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalCount;
    private final int totalPages;
    private final boolean hasPrevious;
    private final boolean hasNext;

    /**
     * @param items        리스트
     * @param page         페이지
     * @param size         한 페이지당 아이템 갯수
     * @param totalCount   전체 갯수
     * @param totalPages   전체 페이지 수
     * @param hasPrevious  이전버튼
     * @param hasNext      다음버튼
     */
    public PageView(List<T> items,
                    int page,
                    int size,
                    long totalCount,
                    int totalPages,
                    boolean hasPrevious,
                    boolean hasNext) {
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
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    public int page() {
        return page;
    }

    public int size() {
        return size;
    }

    public long totalCount() {
        return totalCount;
    }

    public int totalPages() {
        return totalPages;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
