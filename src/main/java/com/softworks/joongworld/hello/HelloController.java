package com.softworks.joongworld.hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("title", "Thymeleaf Test");
        model.addAttribute("message", "안녕, 타임리프!");
        return "hello"; // templates/hello.html 렌더링
    }
}