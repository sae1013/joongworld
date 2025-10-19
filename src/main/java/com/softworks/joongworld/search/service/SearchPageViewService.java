package com.softworks.joongworld.search.service;

import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.search.dto.SearchPageView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SearchPageViewService {

    private static final int DEFAULT_LIMIT = 20;

    private final ProductService productService;

    public SearchPageViewService(ProductService productService) {
        this.productService = productService;
    }

    public SearchPageView buildSearchView(Integer categoryId, String query) {
        List<ProductSummaryView> products = categoryId != null
                ? productService.getRecentProductsByCategory(categoryId, DEFAULT_LIMIT)
                : productService.getRecentProducts(DEFAULT_LIMIT);

        List<CategoryView> categories = productService.getAllCategories();
        String selectedCategoryName = categories.stream()
                .filter(category -> categoryId != null && category.id().equals(categoryId))
                .map(CategoryView::name)
                .findFirst()
                .orElse(null);

        if (categoryId != null && selectedCategoryName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.");
        }

        return new SearchPageView(
                products,
                categories,
                categoryId,
                selectedCategoryName,
                query
        );
    }
}
