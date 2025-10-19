package com.softworks.joongworld.search.dto;

import com.softworks.joongworld.product.dto.CategoryView;
import com.softworks.joongworld.product.dto.ProductSummaryView;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

public record SearchPageView(
        List<ProductSummaryView> products,
        List<CategoryView> categories,
        Integer selectedCategoryId,
        String selectedCategoryName,
        String query
) {

    public SearchPageView {
        Objects.requireNonNull(products, "products must not be null");
        Objects.requireNonNull(categories, "categories must not be null");
    }

    public void applyTo(ModelAndView mav) {
        mav.addObject("products", products);
        mav.addObject("categories", categories);
        mav.addObject("selectedCategoryId", selectedCategoryId);
        mav.addObject("selectedCategoryName", selectedCategoryName);
        mav.addObject("query", query);
    }
}
