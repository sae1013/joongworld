package com.softworks.joongworld.post.controller;

import com.softworks.joongworld.post.dto.PostDetailView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId, Model model) {
        model.addAttribute("product", samplePost(postId));
        return "posts/detail";
    }

    private PostDetailView samplePost(Long postId) {
        return new PostDetailView(
                postId,
                "오메가 아쿠아테라 쿼츠 36mm 은판",
                "시계/주얼리",
                1_800_000,
                "중고 · 가벼운 흠집",
                LocalDateTime.now().minusDays(2),
                List.of(
                        "https://picsum.photos/1200/900?random=1",
                        "https://picsum.photos/1200/900?random=2",
                        "https://picsum.photos/1200/900?random=3"
                )
        );
    }
}
