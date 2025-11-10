package com.softworks.joongworld.comment.controller;

import com.softworks.joongworld.auth.support.SessionAuthenticationFilter;
import com.softworks.joongworld.comment.dto.CommentCreateRequest;
import com.softworks.joongworld.comment.dto.CommentLikeResponse;
import com.softworks.joongworld.comment.dto.CommentResponse;
import com.softworks.joongworld.comment.dto.CommentUpdateRequest;
import com.softworks.joongworld.comment.service.CommentService;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  @GetMapping("/api/products/{productId}/comments")
  public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long productId,
      HttpServletRequest request) {
    LoginUserInfo currentUser = extractLoginUser(request); // 로그인 상태별 권한/노출 제어
    List<CommentResponse> responses = commentService.getComments(productId,
        currentUser); // 좋아요 여부 등 포함

    return ResponseEntity.ok(responses);
  }

  /**
   * 특정 상품에 댓글을 새로 작성
   */
  @PostMapping("/api/products/{productId}/comments")
  public ResponseEntity<CommentResponse> addComment(@PathVariable Long productId,
      @Valid @RequestBody CommentCreateRequest request,
      HttpServletRequest httpRequest) {
    LoginUserInfo currentUser = extractLoginUser(httpRequest); // 작성자 식별
    CommentResponse response = commentService.addComment(productId, request,
        currentUser); // 저장 후 결과 반환

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * 본인이 작성한 댓글 내용을 수정
   */
  @PutMapping("/api/comments/{commentId}")
  public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
      @Valid @RequestBody CommentUpdateRequest request,
      HttpServletRequest httpRequest) {
    LoginUserInfo currentUser = extractLoginUser(httpRequest); // 작성자 본인 검증
    CommentResponse response = commentService.updateComment(commentId, request,
        currentUser); // 수정 반영

    return ResponseEntity.ok(response);
  }

  /**
   * 댓글을 삭제
   */
  @DeleteMapping("/api/comments/{commentId}")
  public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
      HttpServletRequest httpRequest) {
    LoginUserInfo currentUser = extractLoginUser(httpRequest); // 삭제 권한 확인
    commentService.deleteComment(commentId, currentUser); // 서비스에서 권한 검증 후 삭제

    return ResponseEntity.noContent().build();
  }

  /**
   * 댓글 좋아요를 토글 (좋아요/취소)
   */
  @PostMapping("/api/comments/{commentId}/likes")
  public ResponseEntity<CommentLikeResponse> toggleLike(@PathVariable Long commentId,
      HttpServletRequest httpRequest) {
    LoginUserInfo currentUser = extractLoginUser(httpRequest); // 좋아요 토글 주체 확인
    CommentLikeResponse response = commentService.toggleLike(commentId,
        currentUser); // 좋아요/취소 상태 응답

    return ResponseEntity.ok(response);
  }

  private LoginUserInfo extractLoginUser(HttpServletRequest request) {
    Object principal = request.getAttribute(
        SessionAuthenticationFilter.CURRENT_USER_ATTR); // 세션 필터가 저장한 사용자

    if (principal instanceof LoginUserInfo info) {
      return info;
    }

    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication(); // Spring Security fallback

    if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info) {
      return info;
    }

    return LoginUserInfo.empty();
  }
}
