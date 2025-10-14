package com.softworks.joongworld.hello;

import com.softworks.joongworld.hello.mapper.HelloMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloDbController {
    private final HelloMapper helloMapper;

    public HelloDbController(HelloMapper helloMapper) {
        this.helloMapper = helloMapper;
    }

    @GetMapping("/db-ping")
    public String dbPing(Model model) {
        model.addAttribute("title", "DB Ping");
        model.addAttribute("message", "result=" + helloMapper.ping());
        return "hello";
    }
}