package com.softworks.joongworld.product.dto;

import com.softworks.joongworld.user.dto.UserInfoView;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@ToString
public class ProductDetailView {

    private final Long id;
    private final Integer categoryId;
    private final String title;
    private final String categoryName;
    private final Long price;
    private final String region;
    private final Boolean safePay;
    private final Boolean shippingAvailable;
    private final Boolean meetupAvailable;
    private final UserInfoView userInfo;
    private final String conditionStatus;
    private final Long shippingCost;
    private final String description;
    private final OffsetDateTime createdAt;
    private final List<String> images;

    public ProductDetailView(
            Long id,
            Integer categoryId,
            String title,
            String categoryName,
            Long price,
            String region,
            Boolean safePay,
            Boolean shippingAvailable,
            Boolean meetupAvailable,
            Long sellerId,
            String sellerName,
            String sellerNickname,
            Boolean sellerIsAdmin,
            String conditionStatus,
            Long shippingCost,
            String description,
            OffsetDateTime createdAt,
            List<String> images
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.categoryName = categoryName;
        this.price = price;
        this.region = region;
        this.safePay = safePay;
        this.shippingAvailable = shippingAvailable;
        this.meetupAvailable = meetupAvailable;
        this.userInfo = new UserInfoView(sellerId, sellerName, sellerNickname, sellerIsAdmin);
        this.conditionStatus = conditionStatus;
        this.shippingCost = shippingCost;
        this.description = description;
        this.createdAt = createdAt;
        this.images = images;
    }

}
