package com.softworks.joongworld.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "상품명을 입력해 주세요.")
    private String title;

    @NotNull(message = "판매 가격을 입력해 주세요.")
    @Min(value = 1, message = "판매 가격은 1원 이상이어야 합니다.")
    private Long price;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Integer categoryId;

    @NotBlank(message = "거래 지역을 입력해 주세요.")
    private String region;

    @NotBlank(message = "상품 설명을 입력해 주세요.")
    private String description;

    @NotBlank(message = "상품 상태를 선택해 주세요.")
    private String conditionStatus;

    private Boolean safePay = Boolean.FALSE;
    private Boolean shippingAvailable = Boolean.FALSE;
    private Boolean meetupAvailable = Boolean.FALSE;

    private Long shippingCost = 0L;

    private Integer thumbnailIndex = 0;
    private Integer imageCount = 0;

    private List<MultipartFile> images;

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }
}
