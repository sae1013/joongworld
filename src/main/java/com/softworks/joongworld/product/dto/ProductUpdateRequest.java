package com.softworks.joongworld.product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ProductUpdateRequest extends ProductCreateRequest {
    /**
     * 수정 요청에서 제거할 이미지 인덱스 목록
     */
    private List<Integer> removedImages;
}
