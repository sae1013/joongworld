package com.softworks.joongworld.search.controller;


import com.softworks.joongworld.common.pageable.Pageables;
import com.softworks.joongworld.search.service.SearchPageViewService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SearchViewController {

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
                               @RequestParam(value = "size", defaultValue = "" + Pageables.DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = Pageables.from(page, size);
        var view = searchPageViewService.buildSearchView(categoryId, query, nickname, categoryName, title, pageable);
        ModelAndView mav = new ModelAndView("product/list");
        view.applyTo(mav);
        return mav;
    }
}
