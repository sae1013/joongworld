package com.softworks.joongworld.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    private Long parentId;

    @NotBlank(message = "댓글 내용을 입력해 주세요.")
    private String content;
}
