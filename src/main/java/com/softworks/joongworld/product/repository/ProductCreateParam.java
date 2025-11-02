package com.softworks.joongworld.product.repository;

import lombok.Data;

import java.util.List;

@Data
public class ProductCreateParam {
    private Long id;
    private Integer categoryId;
    private Long userId;
    private String title;
    private Long price;
    private String region;
    private Boolean safePay;
    private Boolean shippingAvailable;
    private Boolean meetupAvailable;
    private Long shippingCost;
    private String conditionStatus;
    private String description;
    private String thumbnailUrl;
    private List<String> imageUrls;
    private Integer thumbnailIndex;
    private Integer imageCount;
}
