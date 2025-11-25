package com.softworks.joongworld.common.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 *
 * 모든 페이지의 Default 기본 크기, 최대 크기, 정렬 규칙을 따르도록 공통화.
 */
public final class Pageables {

  public static final int DEFAULT_PAGE_SIZE = 8;
  public static final int MAX_PAGE_SIZE = 50;
  public static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

  private Pageables() {
  }

  /**
   *
   * @param page 1부터 시작하는 페이지 번호
   * @param size 요청한 페이지 크기
   */
  public static Pageable from(int page, int size) {
    int pageNumber = Math.max(page, 1) - 1;
    int pageSize = clampPageSize(size);
    return PageRequest.of(pageNumber, pageSize, DEFAULT_SORT);
  }

  /**
   * 서비스 계층에서 넘어온 파라미터를 정규화 페이지 번호, 크기, 정렬에 기본값을 적용해 유효한 범위(?)를 판단
   */
  public static Pageable sanitize(Pageable pageable) {
    if (pageable == null || pageable.isUnpaged()) {
      return PageRequest.of(0, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
    }

    int pageNumber = Math.max(pageable.getPageNumber(), 0);
    int pageSize = clampPageSize(pageable.getPageSize());
    Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : DEFAULT_SORT;
    return PageRequest.of(pageNumber, pageSize, sort);
  }

  private static int clampPageSize(int requestedSize) {
    if (requestedSize <= 0) {
      return DEFAULT_PAGE_SIZE;
    }
    return Math.min(requestedSize, MAX_PAGE_SIZE);
  }
}
