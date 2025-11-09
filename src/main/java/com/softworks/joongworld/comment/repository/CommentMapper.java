package com.softworks.joongworld.comment.repository;

import com.softworks.joongworld.comment.model.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    int insert(CommentEntity comment);

    CommentEntity findById(@Param("id") Long id);

    List<CommentEntity> findByProductId(@Param("productId") Long productId);

    int updateContent(@Param("id") Long id, @Param("content") String content);

    int softDelete(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);

    int decrementLikeCount(@Param("id") Long id);
}
