package com.softworks.joongworld.comment.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CommentEntity {
    private Long id;
    private Long productId;
    private Long parentId;
    private Integer depth;
    private Long authorId;
    private String authorNickname;
    private String content;
    private Integer likeCount;
    private Boolean isDeleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
