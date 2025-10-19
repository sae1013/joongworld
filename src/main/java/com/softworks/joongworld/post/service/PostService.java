package com.softworks.joongworld.post.service;

import com.softworks.joongworld.post.dto.CategoryView;
import com.softworks.joongworld.post.dto.PostDetailView;
import com.softworks.joongworld.post.dto.PostSummaryView;
import com.softworks.joongworld.post.repository.PostMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {

    private final PostMapper postMapper;

    public PostService(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public List<PostSummaryView> getRecentPosts(int limit) {
        return postMapper.findRecentSummaries(limit);
    }

    public List<PostSummaryView> getRecentPostsByCategory(Integer categoryId, int limit) {
        return postMapper.findRecentSummariesByCategory(categoryId, limit);
    }

    public List<CategoryView> getAllCategories() {
        return postMapper.findAllCategories();
    }

    public PostDetailView getPostDetail(Long postId) {
        PostDetailView detail = postMapper.findDetailById(postId);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return detail;
    }
}
