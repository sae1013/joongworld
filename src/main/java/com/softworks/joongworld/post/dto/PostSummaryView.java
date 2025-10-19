package com.softworks.joongworld.post.dto;

import java.time.LocalDateTime;

/**
 * 게시글 목록이나 카드에서 사용하는 요약 정보 DTO
 * 개발 중에는 더미 데이터를 채우기 위해 사용한다.
 */
public record PostSummaryView(
        Long id,
        String title,
        int price,
        String region,
        LocalDateTime createdAt,
        String thumbnailUrl,
        boolean safePay
) {
}
