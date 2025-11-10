package com.softworks.joongworld.search.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 검색 페이지 요청 파라미터 DTO.
 */
@Getter
@Setter
public class SearchViewRequest {
    private Integer categoryId;
    private String query;
    private Integer page;
    private Integer size;
}
