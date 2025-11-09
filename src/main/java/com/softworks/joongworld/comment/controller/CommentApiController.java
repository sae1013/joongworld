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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentApiController {

    private final CommentService commentService;

    @GetMapping("/products/{productId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long productId,
                                                             HttpServletRequest request) {
        LoginUserInfo currentUser = extractLoginUser(request);
        List<CommentResponse> responses = commentService.getComments(productId, currentUser);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/products/{productId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long productId,
                                                      @Valid @RequestBody CommentCreateRequest request,
                                                      HttpServletRequest httpRequest) {
        LoginUserInfo currentUser = extractLoginUser(httpRequest);
        CommentResponse response = commentService.addComment(productId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,
                                                         @Valid @RequestBody CommentUpdateRequest request,
                                                         HttpServletRequest httpRequest) {
        LoginUserInfo currentUser = extractLoginUser(httpRequest);
        CommentResponse response = commentService.updateComment(commentId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              HttpServletRequest httpRequest) {
        LoginUserInfo currentUser = extractLoginUser(httpRequest);
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<CommentLikeResponse> toggleLike(@PathVariable Long commentId,
                                                          HttpServletRequest httpRequest) {
        LoginUserInfo currentUser = extractLoginUser(httpRequest);
        CommentLikeResponse response = commentService.toggleLike(commentId, currentUser);
        return ResponseEntity.ok(response);
    }

    private LoginUserInfo extractLoginUser(HttpServletRequest request) {
        Object principal = request.getAttribute(SessionAuthenticationFilter.CURRENT_USER_ATTR);
        if (principal instanceof LoginUserInfo info) {
            return info;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUserInfo info) {
            return info;
        }
        return LoginUserInfo.empty();
    }
}
