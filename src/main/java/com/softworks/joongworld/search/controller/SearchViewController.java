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

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(value = "category", required = false) Integer categoryId,
                               @RequestParam(value = "q", required = false) String query,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        Pageable pageable = createPageable(page, size);
        var view = searchPageViewService.buildSearchView(categoryId, query, pageable);
        ModelAndView mav = new ModelAndView("product/list");
        view.applyTo(mav);
        return mav;
    }

    private Pageable createPageable(int page, int size) {
        int pageNumber = Math.max(page, 1) - 1;
        int pageSize = Math.max(1, Math.min(size, MAX_PAGE_SIZE));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
