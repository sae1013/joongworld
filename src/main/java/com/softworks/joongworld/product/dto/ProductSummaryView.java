package com.softworks.joongworld.product.dto;

import com.softworks.joongworld.user.dto.UserInfoView;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * 상품 목록이나 카드에서 사용하는 요약 정보 DTO
 * 개발 중에는 더미 데이터를 채우기 위해 사용한다.
 */
@Getter
@ToString
public class ProductSummaryView {

    private final Long id;
    private final String title;
    private final String categoryName;
    private final Long price;
    private final String region;
    private final OffsetDateTime createdAt;
    private final String thumbnailUrl;
    private final Boolean safePay;
    private final UserInfoView userInfo;

    public ProductSummaryView(
            Long id,
            String title,
            String categoryName,
            Long price,
            String region,
            OffsetDateTime createdAt,
            String thumbnailUrl,
            Boolean safePay,
            Long sellerId,
            String sellerName,
            String sellerNickname,
            Boolean sellerIsAdmin
    ) {
        this.id = id;
        this.title = title;
        this.categoryName = categoryName;
        this.price = price;
        this.region = region;
        this.createdAt = createdAt;
        this.thumbnailUrl = thumbnailUrl;
        this.safePay = safePay;
        this.userInfo = new UserInfoView(sellerId, sellerName, sellerNickname, sellerIsAdmin);
    }
}
