package com.softworks.joongworld.post.repository;

import com.softworks.joongworld.post.dto.CategoryView;
import com.softworks.joongworld.post.dto.PostDetailView;
import com.softworks.joongworld.post.dto.PostSummaryView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {

    List<PostSummaryView> findRecentSummaries(@Param("limit") int limit);

    List<PostSummaryView> findRecentSummariesByCategory(@Param("categoryId") Integer categoryId,
                                                        @Param("limit") int limit);

    PostDetailView findDetailById(@Param("postId") Long postId);

    List<CategoryView> findAllCategories();
}
