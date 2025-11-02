package com.softworks.joongworld.product.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ProductDetailView(
        Long id,
        Integer categoryId,
        String title,
        String categoryName,
        Long price,
        String condition,
        String region,
        Boolean safePay,
        Boolean shippingAvailable,
        Boolean meetupAvailable,
        String shippingCostText,
        String description,
        OffsetDateTime createdAt,
        List<String> images
) {
}
