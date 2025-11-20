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
    var users = adminUserService.getRecentUsers();
    var categories = adminCategoryService.getAll();
    var userReports = adminReportService.getRecentUserReports();
    var productReports = adminReportService.getRecentProductReports();
    var pending = adminUserService.getPendingApprovals();
    mv.addObject("dashboardUsers", users);
    mv.addObject("dashboardCategories", categories);
    mv.addObject("dashboardUserReports", userReports);
    mv.addObject("dashboardProductReports", productReports);
    mv.addObject("dashboardPendingCount", pending != null ? pending.size() : 0);
    mv.addObject("adminActiveMenu", "dashboard");
    return mv;
  }

  @GetMapping("/admin/users")
  public ModelAndView manageUsers() {
    ModelAndView mv = new ModelAndView("admin/users");
    mv.addObject("users", adminUserService.getRecentUsers());
    mv.addObject("adminActiveMenu", "users");
    return mv;
  }

  @GetMapping("/admin/users/approve")
  public ModelAndView approveUsers() {
    ModelAndView mv = new ModelAndView("admin/approve");
    mv.addObject("pendingManagers", adminUserService.getPendingApprovals());
    mv.addObject("adminActiveMenu", "approvals");
    return mv;
  }

  @GetMapping("/admin/categories")
  public ModelAndView manageCategories() {
    ModelAndView mv = new ModelAndView("admin/categories");
    mv.addObject("categories", adminCategoryService.getAll());
    mv.addObject("adminActiveMenu", "categories");
    return mv;
  }

  @GetMapping("/admin/reports/users")
  public ModelAndView userReports() {
    ModelAndView mv = new ModelAndView("admin/user-reports");
    mv.addObject("reports", adminReportService.getRecentUserReports());
    mv.addObject("adminActiveMenu", "userReports");
    return mv;
  }

  @GetMapping("/admin/reports/products")
  public ModelAndView productReports() {
    ModelAndView mv = new ModelAndView("admin/product-reports");
    mv.addObject("reports", adminReportService.getRecentProductReports());
    mv.addObject("adminActiveMenu", "productReports");
    return mv;
  }
}
