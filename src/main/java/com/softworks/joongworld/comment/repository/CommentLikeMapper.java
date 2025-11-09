package com.softworks.joongworld.comment.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentLikeMapper {

    int insert(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int delete(@Param("commentId") Long commentId, @Param("userId") Long userId);

    int countByCommentAndUser(@Param("commentId") Long commentId, @Param("userId") Long userId);

    List<Long> findLikedCommentIds(@Param("commentIds") List<Long> commentIds,
                                   @Param("userId") Long userId);
}
