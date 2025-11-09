package com.softworks.joongworld.comment.service;

import com.softworks.joongworld.comment.dto.CommentCreateRequest;
import com.softworks.joongworld.comment.dto.CommentLikeResponse;
import com.softworks.joongworld.comment.dto.CommentResponse;
import com.softworks.joongworld.comment.dto.CommentUpdateRequest;
import com.softworks.joongworld.comment.model.CommentEntity;
import com.softworks.joongworld.comment.repository.CommentLikeMapper;
import com.softworks.joongworld.comment.repository.CommentMapper;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String DELETED_MESSAGE = "삭제된 댓글입니다.";

    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long productId, LoginUserInfo user) {
        List<CommentEntity> entities = commentMapper.findByProductId(productId);
        List<Long> commentIds = entities.stream().map(CommentEntity::getId).toList();

        Set<Long> likedCommentIds = Collections.emptySet();
        Long currentUserId = (user != null) ? user.getId() : null;
        if (user != null && user.getId() != null && !commentIds.isEmpty()) {
            likedCommentIds = commentLikeMapper.findLikedCommentIds(commentIds, user.getId())
                    .stream()
                    .collect(Collectors.toSet());
        }

        Map<Long, CommentResponse> responseMap = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        for (CommentEntity entity : entities) {
            CommentResponse response = toResponse(
                    entity,
                    likedCommentIds.contains(entity.getId()),
                    currentUserId);
            responseMap.put(response.getId(), response);
        }

        for (CommentResponse response : responseMap.values()) {
            if (response.getParentId() == null) {
                roots.add(response);
            } else {
                CommentResponse parent = responseMap.get(response.getParentId());
                if (parent != null) {
                    parent.getReplies().add(response);
                }
            }
        }
        return roots;
    }

    @Transactional
    public CommentResponse addComment(Long productId,
                                      CommentCreateRequest request,
                                      LoginUserInfo user) {
        requireAuthenticated(user);

        CommentEntity parent = null;
        if (request.getParentId() != null) {
            parent = commentMapper.findById(request.getParentId());
            if (parent == null || !Objects.equals(parent.getProductId(), productId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대상 댓글을 찾을 수 없습니다.");
            }
        }

        CommentEntity entity = new CommentEntity();
        entity.setProductId(productId);
        entity.setParentId(request.getParentId());
        entity.setDepth(parent == null ? 0 : parent.getDepth() + 1);
        entity.setAuthorId(user.getId());
        entity.setContent(request.getContent().trim());

        int inserted = commentMapper.insert(entity);
        if (inserted != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 작성에 실패했습니다.");
        }

        CommentEntity saved = commentMapper.findById(entity.getId());
        return toResponse(saved, false, user.getId());
    }

    @Transactional
    public CommentResponse updateComment(Long commentId,
                                         CommentUpdateRequest request,
                                         LoginUserInfo user) {
        requireAuthenticated(user);
        CommentEntity comment = ensureCommentOwnedBy(commentId, user);
        if (comment.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
        }

        int updated = commentMapper.updateContent(commentId, request.getContent().trim());
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 수정에 실패했습니다.");
        }
        CommentEntity saved = commentMapper.findById(commentId);
        return toResponse(saved, false, user.getId());
    }

    @Transactional
    public void deleteComment(Long commentId, LoginUserInfo user) {
        requireAuthenticated(user);
        ensureCommentOwnedBy(commentId, user);
        int deleted = commentMapper.softDelete(commentId);
        if (deleted != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다.");
        }
    }

    @Transactional
    public CommentLikeResponse toggleLike(Long commentId, LoginUserInfo user) {
        requireAuthenticated(user);
        CommentEntity comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
        }

        boolean alreadyLiked = commentLikeMapper.countByCommentAndUser(commentId, user.getId()) > 0;
        boolean liked;
        int affected;
        if (alreadyLiked) {
            affected = commentLikeMapper.delete(commentId, user.getId());
            if (affected > 0) {
                commentMapper.decrementLikeCount(commentId);
            }
            liked = false;
        } else {
            affected = commentLikeMapper.insert(commentId, user.getId());
            if (affected > 0) {
                commentMapper.incrementLikeCount(commentId);
            }
            liked = true;
        }

        CommentEntity updated = commentMapper.findById(commentId);
        return new CommentLikeResponse(commentId, updated.getLikeCount(), liked);
    }

    private CommentResponse toResponse(CommentEntity entity, boolean likedByMe, Long currentUserId) {
        CommentResponse response = new CommentResponse();
        response.setId(entity.getId());
        response.setProductId(entity.getProductId());
        response.setParentId(entity.getParentId());
        response.setDepth(entity.getDepth());
        response.setAuthorId(entity.getAuthorId());
        response.setAuthorNickname(entity.getAuthorNickname());
        response.setContent(entity.getContent());
        response.setDeleted(Boolean.TRUE.equals(entity.getIsDeleted()));
        response.setLikeCount(entity.getLikeCount() == null ? 0 : entity.getLikeCount());
        response.setLikedByMe(likedByMe);
        response.setOwner(currentUserId != null && currentUserId.equals(entity.getAuthorId()));
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        if (response.isDeleted()) {
            response.setContent(DELETED_MESSAGE);
        }
        return response;
    }

    private void requireAuthenticated(LoginUserInfo user) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    private CommentEntity ensureCommentOwnedBy(Long commentId, LoginUserInfo user) {
        CommentEntity comment = commentMapper.findById(commentId);
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
        }
        if (!Objects.equals(comment.getAuthorId(), user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 댓글만 수정할 수 있습니다.");
        }
        return comment;
    }
}
