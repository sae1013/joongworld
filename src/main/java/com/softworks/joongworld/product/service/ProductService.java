package com.softworks.joongworld.product.service;

import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.product.repository.ProductMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<ProductSummaryView> getRecentProducts(int limit) {
        return productMapper.findRecentSummaries(limit);
    }

    public List<ProductSummaryView> getRecentProductsByCategory(Integer categoryId, int limit) {
        return productMapper.findRecentSummariesByCategory(categoryId, limit);
    }

    public List<CategoryView> getAllCategories() {
        return productMapper.findAllCategories();
    }

    public ProductDetailView getProductDetail(Long productId) {
        ProductDetailView detail = productMapper.findDetailById(productId);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return detail;
    }
}
