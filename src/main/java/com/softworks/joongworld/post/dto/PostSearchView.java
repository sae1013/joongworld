package com.softworks.joongworld.post.dto;

import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

public record PostSearchView(
        List<PostSummaryView> posts,
        List<CategoryView> categories,
        Integer selectedCategoryId,
        String selectedCategoryName,
        String query
) {
    
    public void applyTo(ModelAndView mav) {
        mav.addObject("posts", posts);
        mav.addObject("categories", categories);
        mav.addObject("selectedCategoryId", selectedCategoryId);
        mav.addObject("selectedCategoryName", selectedCategoryName);
        mav.addObject("query", query);
    }
}
