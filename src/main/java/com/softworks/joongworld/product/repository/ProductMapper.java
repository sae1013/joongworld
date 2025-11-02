package com.softworks.joongworld.product.repository;

import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.user.dto.UserInfoView;
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

    void insertProduct(ProductCreateParam param);

    List<ProductSummaryView> findSummariesByUserId(@Param("userId") Long userId);

    int updateProduct(ProductUpdateParam param);

    UserInfoView findProductOwner(@Param("productId") Long productId);

    int deleteProduct(@Param("productId") Long productId);
}
