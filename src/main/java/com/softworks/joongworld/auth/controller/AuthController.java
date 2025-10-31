package com.softworks.joongworld.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {

    @GetMapping("/signup")
    public ModelAndView signupForm() {
        return new ModelAndView("auth/signup");
    }
}

