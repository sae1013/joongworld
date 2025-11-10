package com.softworks.joongworld.common.pageable;

import org.springframework.data.domain.Page;

public final class PageViewMapper {

    private PageViewMapper() {
    }

    public static <T> PageView<T> from(Page<T> page) {
        return new PageView<>(
                page.getContent(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasPrevious(),
                page.hasNext()
        );
    }
}
