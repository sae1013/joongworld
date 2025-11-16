package com.softworks.joongworld.admin.controller.view;

import com.softworks.joongworld.admin.service.AdminCategoryService;
import com.softworks.joongworld.admin.service.AdminReportService;
import com.softworks.joongworld.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping()
public class AdminViewController {

    private final AdminCategoryService adminCategoryService;
    private final AdminReportService adminReportService;
    private final AdminUserService adminUserService;

    @GetMapping("/admin/signup")
    public ModelAndView signupForm() {
        return new ModelAndView("admin/signup");
    }

    @GetMapping("/admin/login")
    public ModelAndView loginForm() {
        return new ModelAndView("admin/login");
    }

    @GetMapping("/admin/dashboard")
    public ModelAndView dashboard() {
        ModelAndView mv = new ModelAndView("admin/dashboard");
        mv.addObject("dashboardCategories", adminCategoryService.getAll());
        mv.addObject("dashboardUserReports", adminReportService.getRecentUserReports());
        mv.addObject("dashboardProductReports", adminReportService.getRecentProductReports());
        mv.addObject("dashboardUsers", adminUserService.getRecentUsers());
        return mv;
    }
}
