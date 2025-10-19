package com.softworks.joongworld.post.controller;

import com.softworks.joongworld.post.service.PostSearchViewService;
import com.softworks.joongworld.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class PostController {

    private final PostService postService;
    private final PostSearchViewService postSearchViewService;

    public PostController(PostService postService,
                          PostSearchViewService postSearchViewService) {
        this.postService = postService;
        this.postSearchViewService = postSearchViewService;
    }

    @GetMapping("/search")
    public ModelAndView search(@RequestParam(value = "category", required = false) Integer categoryId,
                               @RequestParam(value = "q", required = false) String query) {
        var view = postSearchViewService.buildSearchView(categoryId, query);
        ModelAndView mav = new ModelAndView("posts/list");
        view.applyTo(mav);
        return mav;
    }

    @GetMapping("/posts/{postId}")
    public ModelAndView detail(@PathVariable Long postId) {
        ModelAndView mav = new ModelAndView("posts/detail");
        mav.addObject("product", postService.getPostDetail(postId));
        return mav;
    }
}
