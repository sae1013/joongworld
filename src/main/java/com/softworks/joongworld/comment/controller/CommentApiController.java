package com.softworks.joongworld.comment.controller;

import com.softworks.joongworld.auth.support.CurrentUser;
import com.softworks.joongworld.comment.dto.CommentCreateRequest;
import com.softworks.joongworld.comment.dto.CommentLikeResponse;
import com.softworks.joongworld.comment.dto.CommentResponse;
import com.softworks.joongworld.comment.dto.CommentUpdateRequest;
import com.softworks.joongworld.comment.service.CommentService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    /**
     * 특정 상품의 댓글 목록을 조회
     */
    @GetMapping(value = "/api/products/{productId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long productId,
        @CurrentUser LoginUserInfo currentUser) {
        List<CommentResponse> responses = commentService.getComments(productId,
            currentUser); // 좋아요 여부 등 포함

        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 상품에 댓글을 새로 작성
     */
    @PostMapping(value = "/api/products/{productId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long productId,
        @Valid @RequestBody CommentCreateRequest request,
        @CurrentUser LoginUserInfo currentUser) {
        CommentResponse response = commentService.addComment(productId, request,
            currentUser); // 저장 후 결과 반환

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 본인이 작성한 댓글 내용을 수정
     */
    @PutMapping(value = "/api/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
        @Valid @RequestBody CommentUpdateRequest request,
        @CurrentUser LoginUserInfo currentUser) {
        CommentResponse response = commentService.updateComment(commentId, request,
            currentUser); // 수정 반영

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글을 삭제
     */
    @DeleteMapping(value = "/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
        @CurrentUser LoginUserInfo currentUser) {
        commentService.deleteComment(commentId, currentUser); // 서비스에서 권한 검증 후 삭제

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 좋아요를 토글 (좋아요/취소)
     */
    @PostMapping(value = "/api/comments/{commentId}/likes")
    public ResponseEntity<CommentLikeResponse> toggleLike(@PathVariable Long commentId,
        @CurrentUser LoginUserInfo currentUser) {
        CommentLikeResponse response = commentService.toggleLike(commentId,
            currentUser); // 좋아요/취소 상태 응답

        return ResponseEntity.ok(response);
    }
}
