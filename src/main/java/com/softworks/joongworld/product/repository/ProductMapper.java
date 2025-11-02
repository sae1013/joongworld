package com.softworks.joongworld.product.repository;

import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<ProductSummaryView> findSummaries(@Param("categoryId") Integer categoryId,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);

    long countSummaries(@Param("categoryId") Integer categoryId);

    ProductDetailView findDetailById(@Param("productId") Long productId);
}
