package com.softworks.joongworld.search.controller;

import com.softworks.joongworld.search.service.SearchPageViewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class SearchController {

    private final SearchPageViewService searchPageViewService;

    public SearchController(SearchPageViewService searchPageViewService) {
        this.searchPageViewService = searchPageViewService;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(value = "category", required = false) Integer categoryId,
                               @RequestParam(value = "q", required = false) String query) {
        var view = searchPageViewService.buildSearchView(categoryId, query);
        ModelAndView mav = new ModelAndView("product/list");
        view.applyTo(mav);
        return mav;
    }
}
