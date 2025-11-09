package com.softworks.joongworld.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeResponse {
    private Long commentId;
    private int likeCount;
    private boolean liked;
}
