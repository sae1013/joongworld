package com.softworks.joongworld.search.dto;

import com.softworks.joongworld.global.pagination.PageView;
import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

public record SearchPageView(
        PageView<ProductSummaryView> productPage,
        List<CategoryView> categories,
        Integer selectedCategoryId,
        String selectedCategoryName,
        String query
) {

    public SearchPageView {
        Objects.requireNonNull(productPage, "productPage must not be null");
        Objects.requireNonNull(categories, "categories must not be null");
    }

    public void applyTo(ModelAndView mav) {
        mav.addObject("products", productPage.items());
        mav.addObject("page", productPage);
        mav.addObject("categories", categories);
        mav.addObject("selectedCategoryId", selectedCategoryId);
        mav.addObject("selectedCategoryName", selectedCategoryName);
        mav.addObject("query", query);
        mav.addObject("currentPage", productPage.page());
        mav.addObject("totalPages", productPage.totalPages());
        mav.addObject("totalCount", productPage.totalCount());
        mav.addObject("pageSize", productPage.size());
        mav.addObject("hasPrevious", productPage.hasPrevious());
        mav.addObject("hasNext", productPage.hasNext());
    }

    public boolean showPagination() {
        return productPage.totalPages() > 1;
    }

    public boolean hasPrevious() {
        return productPage.hasPrevious();
    }

    public boolean hasNext() {
        return productPage.hasNext();
    }
}
