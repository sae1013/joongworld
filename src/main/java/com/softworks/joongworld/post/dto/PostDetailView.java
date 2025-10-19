package com.softworks.joongworld.post.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailView(
        Long id,
        String title,
        String category,
        int price,
        String condition,
        LocalDateTime createdAt,
        List<String> images
) {
}
