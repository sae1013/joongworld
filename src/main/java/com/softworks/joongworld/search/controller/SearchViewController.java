package com.softworks.joongworld.search.controller;


import com.softworks.joongworld.search.service.SearchPageViewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SearchViewController {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;
    private final SearchPageViewService searchPageViewService;

    /**
     * 검색을 통한 상품보기 페이지 렌더링
     * @param categoryId 상품카테고리 ID
     * @param query 쿼리파라미터
     * @param page 페이지 번호
     * @param size 한 페이지당 요소갯수
     * @return
     */
    @GetMapping("/search")
    public ModelAndView search(@RequestParam(value = "category", required = false) Integer categoryId,
                               @RequestParam(value = "q", required = false) String query,
                               @RequestParam(value = "nickname", required = false) String nickname,
                               @RequestParam(value = "categoryName", required = false) String categoryName,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = createPageable(page, size);
        var view = searchPageViewService.buildSearchView(categoryId, query, nickname, categoryName, title, pageable);
        ModelAndView mav = new ModelAndView("product/list");
        view.applyTo(mav);
        return mav;
    }

    // TODO: 페이지네이션 공통화 작업 후 이동 예정
    private Pageable createPageable(int page, int size) {
        int pageNumber = Math.max(page, 1) - 1;
        int pageSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
