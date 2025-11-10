package com.softworks.joongworld.common.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility helpers for building and sanitizing {@link Pageable} instances so every page endpoint
 * follows the same default size, maximum size, and sort rules.
 */
public final class Pageables {

  public static final int DEFAULT_PAGE_SIZE = 3;
  public static final int MAX_PAGE_SIZE = 50;
  public static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

  private Pageables() {
  }

  /**
   * Builds a {@link Pageable} from page/size parameters typically received from controllers.
   *
   * @param page 1-based page number
   * @param size requested page size
   */
  public static Pageable from(int page, int size) {
    int pageNumber = Math.max(page, 1) - 1;
    int pageSize = clampPageSize(size);
    return PageRequest.of(pageNumber, pageSize, DEFAULT_SORT);
  }

  /**
   * Normalizes incoming {@link Pageable} objects from service layer callers. Ensures page number,
   * size, and sort all have sane defaults.
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
