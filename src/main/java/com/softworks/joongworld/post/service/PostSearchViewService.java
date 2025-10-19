package com.softworks.joongworld.post.service;

import com.softworks.joongworld.post.dto.CategoryView;
import com.softworks.joongworld.post.dto.PostSearchView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostSearchViewService {

    private static final int DEFAULT_LIMIT = 20;

    private final PostService postService;

    public PostSearchViewService(PostService postService) {
        this.postService = postService;
    }

    public PostSearchView buildSearchView(Integer categoryId, String query) {
        var posts = categoryId != null
                ? postService.getRecentPostsByCategory(categoryId, DEFAULT_LIMIT)
                : postService.getRecentPosts(DEFAULT_LIMIT);

        List<CategoryView> categories = postService.getAllCategories();
        String selectedCategoryName = categories.stream()
                .filter(category -> categoryId != null && category.id().equals(categoryId))
                .map(CategoryView::name)
                .findFirst()
                .orElse(null);

        if (categoryId != null && selectedCategoryName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.");
        }

        return new PostSearchView(
                posts,
                categories,
                categoryId,
                selectedCategoryName,
                query
        );
    }
}
