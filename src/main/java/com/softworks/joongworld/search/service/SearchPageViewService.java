package com.softworks.joongworld.search.service;

import com.softworks.joongworld.global.pagination.PageViewMapper;
import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.product.service.ProductService;
import com.softworks.joongworld.search.dto.SearchPageView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SearchPageViewService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final ProductService productService;

    public SearchPageViewService(ProductService productService) {
        this.productService = productService;
    }

    public SearchPageView buildSearchView(Integer categoryId, String query, Pageable pageable) {
        Pageable effectivePageable = ensurePageable(pageable);
        Page<ProductSummaryView> productPage = productService.getProductPage(categoryId, effectivePageable);

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
                PageViewMapper.from(productPage),
                categories,
                categoryId,
                selectedCategoryName,
                query
        );
    }

    private Pageable ensurePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        int pageSize = pageable.getPageSize();
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        int pageNumber = Math.max(pageable.getPageNumber(), 0);
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
