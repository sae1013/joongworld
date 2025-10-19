package com.softworks.joongworld.post.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record PostDetailView(
        Long id,
        String title,
        String categoryName,
        Integer price,
        String condition,
        String region,
        Boolean safePay,
        String description,
        OffsetDateTime createdAt,
        List<String> images
) {
}
