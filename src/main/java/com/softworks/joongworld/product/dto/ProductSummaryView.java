package com.softworks.joongworld.product.dto;

import java.time.OffsetDateTime;

/**
 * 상품 목록이나 카드에서 사용하는 요약 정보 DTO
 * 개발 중에는 더미 데이터를 채우기 위해 사용한다.
 */
public record ProductSummaryView(
        Long id,
        String title,
        String categoryName,
        Long price,
        String region,
        OffsetDateTime createdAt,
        String thumbnailUrl,
        Boolean safePay
) {
}
