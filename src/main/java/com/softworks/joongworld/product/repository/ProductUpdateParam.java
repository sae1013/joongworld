package com.softworks.joongworld.product.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductUpdateParam extends ProductCreateParam {
    /**
     * 삭제할 이미지 인덱스 목록 (요청에서 전달되는 removed_images)
     */
//    private List<Integer> removedImages;
}
