package com.softworks.joongworld.comment.service;

import com.softworks.joongworld.comment.dto.CommentCreateRequest;
import com.softworks.joongworld.comment.dto.CommentLikeResponse;
import com.softworks.joongworld.comment.dto.CommentResponse;
import com.softworks.joongworld.comment.dto.CommentUpdateRequest;
import com.softworks.joongworld.comment.model.CommentEntity;
import com.softworks.joongworld.comment.repository.CommentLikeMapper;
import com.softworks.joongworld.comment.repository.CommentMapper;
import com.softworks.joongworld.common.auth.RequireLogin;
import com.softworks.joongworld.user.dto.LoginUserInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 댓글/ 댓글 좋아요 도메인 서비스
 */
@Service
@RequiredArgsConstructor
public class CommentService {

  // 댓글을 삭제했을 때 노출할 메시지
  private static final String DELETED_MESSAGE = "삭제된 댓글입니다.";

  // 댓글 CRUD Mapper
  private final CommentMapper commentMapper;

  //댓글 좋아요 CRUD Mapper
  private final CommentLikeMapper commentLikeMapper;


  /**
   *
   * @param productId
   * @param user
   * @return 최상위 (루트) 댓글 목록(각 루트는 대댓글 리스트를 포함하는 형식)
   */
  @Transactional(readOnly = true)
  public List<CommentResponse> getComments(Long productId, LoginUserInfo user) {
    // 상품의 전체 댓글을 조회
    List<CommentEntity> entities = commentMapper.findByProductId(productId);
    System.out.println(entities);

    // 상품에 달린 댓글 ID만 뽑아옴 (좋아요 조회용 ID)
    List<Long> commentIds = entities.stream().map(CommentEntity::getId).toList();
    System.out.println(commentIds);

    // 좋아요 누른 댓글 ID만 저장한다.
    Set<Long> likedCommentIds = Collections.emptySet();
    Long currentUserId = (user != null) ? user.getId() : null;

    // 로그인 상태 && 댓글이 1개 이상일 때만 좋아요를 조회한다.
    if (user != null && user.getId() != null && !commentIds.isEmpty()) {
      likedCommentIds = commentLikeMapper.findLikedCommentIds(commentIds, user.getId())
          .stream()
          .collect(Collectors.toSet());
    }

    // id -> response에 매핑
    Map<Long, CommentResponse> responseMap = new HashMap<>();
    List<CommentResponse> roots = new ArrayList<>(); // 최상위 댓글만 모아두는 리스트

    for (CommentEntity entity : entities) {
      CommentResponse response = toResponse(
          entity,
          likedCommentIds.contains(entity.getId()), // 내가 좋아요를 눌렀는지 여부를 판단
          currentUserId);
      responseMap.put(response.getId(), response);
    }

    // parentId를 기준으로 트리를 구성한다.
    for (CommentResponse response : responseMap.values()) {

      if (response.getParentId() == null) {
        roots.add(response); // parentId 가 없으면 루트 댓글
      } else {
        // 부모가 있으면 부모의 replies 리스트에 자식으로 추가
        CommentResponse parent = responseMap.get(response.getParentId());
        if (parent != null) {
          parent.getReplies().add(response); // 부모의 대댓글 목록에 추가
        }
      }
    }
    System.out.println(roots);
    return roots;
  }

  /**
   * 새 댓글을 저장하고 depth를 설정한다.
   */
  @RequireLogin
  @Transactional
  public CommentResponse addComment(
      Long productId,
      CommentCreateRequest request,
      LoginUserInfo user
  ) {

    CommentEntity parent = null; // parentId 가 있으면 대댓글이므로 부모 검증

    // 대댓글인 경우 부모를 조회하여 동일 상품에 대한 댓글인지 확인.
    if (request.getParentId() != null) {
      parent = commentMapper.findById(request.getParentId());

      // 부모가 없거나, 다른 상품에 대한 댓글일 때
      if (parent == null || !Objects.equals(parent.getProductId(), productId)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대상 댓글을 찾을 수 없습니다.");
      }
    }

    // 등록을 위한 object factory 작업
    CommentEntity entity = new CommentEntity();
    entity.setProductId(productId);
    entity.setParentId(request.getParentId());
    entity.setDepth(parent == null ? 0 : parent.getDepth() + 1); // 최상위=0, 대댓글은 부모 depth + 1
    entity.setAuthorId(user.getId());
    entity.setContent(request.getContent().trim());

    int inserted = commentMapper.insert(entity);

    if (inserted != 1) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 작성에 실패했습니다.");
    }

    CommentEntity saved = commentMapper.findById(entity.getId());
    return toResponse(saved, false, user.getId());
  }

  /**
   * 사용자가 작성한 댓글 내용을 수정한다.
   */
  @RequireLogin
  @Transactional
  public CommentResponse updateComment(
      Long commentId,
      CommentUpdateRequest request,
      LoginUserInfo user
  ) {
    CommentEntity comment = ensureCommentOwnedBy(commentId, user); // 작성자 일치 여부 확인

    if (comment.getIsDeleted()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 댓글입니다.");
    }

    int updated = commentMapper.updateContent(commentId, request.getContent().trim()); // 본문 갱신

    if (updated != 1) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 수정에 실패했습니다.");
    }
    CommentEntity saved = commentMapper.findById(commentId);

    return toResponse(saved, false, user.getId());
  }

  /**
   * 댓글을 소프트 삭제한다. (is_deleted 플래그만 변경)
   */
  @RequireLogin
  @Transactional
  public void deleteComment(Long commentId, LoginUserInfo user) {
    ensureCommentOwnedBy(commentId, user); // 작성자만 삭제 가능
    int deleted = commentMapper.softDelete(commentId);
    if (deleted != 1) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다.");
    }
  }

  /**
   * 댓글 좋아요를 토글하고 현재 집계 반환한다.
   */
  @RequireLogin
  @Transactional
  public CommentLikeResponse toggleLike(Long commentId, LoginUserInfo user) {
    CommentEntity comment = commentMapper.findById(commentId);
    if (comment == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
    }

    boolean alreadyLiked =
        commentLikeMapper.countByCommentAndUser(commentId, user.getId()) > 0; // 현재 상태 파악
    boolean liked;
    int affected;
    // 이미 좋아요를 누른상태는 취소, 아닌상태는 증가
    if (alreadyLiked) {
      affected = commentLikeMapper.delete(commentId, user.getId());

      if (affected > 0) {
        commentMapper.decrementLikeCount(commentId);
      }
      liked = false;
    } else {
      affected = commentLikeMapper.insert(commentId, user.getId());
      if (affected > 0) {
        commentMapper.incrementLikeCount(commentId); // 새로 좋아요를 누른 경우 카운트 증가
      }
      liked = true;
    }

    CommentEntity updated = commentMapper.findById(commentId); // 최신 좋아요 수 재조회
    return new CommentLikeResponse(commentId, updated.getLikeCount(), liked);
  }

  /**
   * 데이터를 조합하여 최종 Comment 객체를 생성
   *
   * @param entity        전체 댓글 객체
   * @param likedByMe     : 좋아요 눌렀는지 여부
   * @param currentUserId : 로그인유저 ID
   * @return
   */
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

  /**
   * 댓글이 존재하는지, 그리고 요청자가 작성자인지 검증한다.
   */
  private CommentEntity ensureCommentOwnedBy(Long commentId, LoginUserInfo user) {
    CommentEntity comment = commentMapper.findById(commentId);

    if (comment == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.");
    }

    if (!Objects.equals(comment.getAuthorId(), user.getId())) { // 다른 사용자가 수정/삭제 요청한 경우
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 댓글만 수정할 수 있습니다.");
    }

    return comment;
  }
}
