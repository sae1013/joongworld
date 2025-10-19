package com.softworks.joongworld.post.controller;

import com.softworks.joongworld.post.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("posts/list");
        mav.addObject("posts", postService.getRecentPosts(20));
        return mav;
    }

    @GetMapping("/{postId}")
    public ModelAndView detail(@PathVariable Long postId) {
        ModelAndView mav = new ModelAndView("posts/detail");
        mav.addObject("product", postService.getPostDetail(postId));
        return mav;
    }
}
