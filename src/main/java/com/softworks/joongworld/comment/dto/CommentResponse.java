package com.softworks.joongworld.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long productId;
    private Long parentId;
    private Integer depth;
    private Long authorId;
    private String authorNickname;
    private String content;
    private boolean deleted;
    private int likeCount;
    private boolean likedByMe;
    private boolean owner;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<CommentResponse> replies = new ArrayList<>();
}
