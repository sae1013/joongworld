package com.softworks.joongworld.product.repository;

import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<ProductSummaryView> findRecentSummaries(@Param("limit") int limit);

    List<ProductSummaryView> findRecentSummariesByCategory(@Param("categoryId") Integer categoryId,
                                                           @Param("limit") int limit);

    ProductDetailView findDetailById(@Param("productId") Long productId);

    List<CategoryView> findAllCategories();
}
