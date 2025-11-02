package com.softworks.joongworld.product.service;

import com.softworks.joongworld.product.dto.ProductDetailView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import com.softworks.joongworld.product.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final ProductMapper productMapper;

    public Page<ProductSummaryView> getProductPage(Integer categoryId, Pageable pageable) {
        Pageable effective = normalizePageable(pageable);
        long totalCount = productMapper.countSummaries(categoryId);

        if (totalCount == 0) {
            return new PageImpl<>(List.of(), effective, totalCount);
        }

        if (effective.getOffset() >= totalCount) {
            int lastPageIndex = (int) ((totalCount - 1) / effective.getPageSize());
            effective = PageRequest.of(lastPageIndex, effective.getPageSize(), effective.getSort());
        }

        int offset = (int) effective.getOffset();
        List<ProductSummaryView> items = productMapper.findSummaries(categoryId, effective.getPageSize(), offset);

        return new PageImpl<>(items, effective, totalCount);
    }

    public ProductDetailView getProductDetail(Long productId) {
        ProductDetailView product = productMapper.findDetailById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
        return product;
    }

    private Pageable normalizePageable(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        int pageNumber = Math.max(pageable.getPageNumber(), 0);
        int pageSize = pageable.getPageSize();
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
