package com.softworks.joongworld.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home/index");
        return mav;
    }
}
