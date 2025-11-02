package com.softworks.joongworld.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthViewController {

    @GetMapping("auth/signup")
    public ModelAndView signupForm() {
        return new ModelAndView("auth/signup");
    }

    @GetMapping("auth/login")
    public ModelAndView loginForm() {
        return new ModelAndView("auth/login");
    }

}
