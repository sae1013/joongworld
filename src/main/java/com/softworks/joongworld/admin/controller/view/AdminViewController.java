package com.softworks.joongworld.admin.controller.view;

import com.softworks.joongworld.admin.service.AdminCategoryService;
import com.softworks.joongworld.admin.service.AdminReportService;
import com.softworks.joongworld.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

  private final AdminCategoryService adminCategoryService;
  private final AdminReportService adminReportService;
  private final AdminUserService adminUserService;

  /**
   * 어드민 회원가입 페이지 뷰
   *
   * @return
   */
  @GetMapping("/admin/signup")
  public ModelAndView signupForm() {
    return new ModelAndView("admin/signup");
  }

  /**
   * 어드민 대시보드 뷰 (카테고리관리 / 회원관리 / 신고관리 )
   *
   * @return
   */
  @GetMapping("/admin/dashboard")
  public ModelAndView dashboard() {
    ModelAndView mv = new ModelAndView("admin/dashboard");
    mv.addObject("dashboardCategories", adminCategoryService.getAll());
    mv.addObject("dashboardUserReports", adminReportService.getRecentUserReports());
    mv.addObject("dashboardProductReports", adminReportService.getRecentProductReports());
    mv.addObject("dashboardUsers", adminUserService.getRecentUsers());
    mv.addObject("adminActiveMenu", "dashboard");
    return mv;
  }

  @GetMapping("/admin/users/approve")
  public ModelAndView approveUsers() {
    ModelAndView mv = new ModelAndView("admin/approve");
    mv.addObject("pendingManagers", adminUserService.getPendingApprovals());
    mv.addObject("adminActiveMenu", "approvals");
    return mv;
  }
}
