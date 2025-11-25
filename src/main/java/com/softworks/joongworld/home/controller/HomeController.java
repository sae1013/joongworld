package com.softworks.joongworld.home.controller;

import com.softworks.joongworld.category.service.CategoryService;
import com.softworks.joongworld.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class HomeController {

  private final CategoryService categoryService;
  private final ProductService productService;

  /**
   * 홈화면 뷰 컨트롤러
   *
   * @return
   */
  @GetMapping({"/", "/home"})
  public ModelAndView home() {
    ModelAndView mav = new ModelAndView("home/index");
    mav.addObject("homeCategories", categoryService.getAllCategories());
    mav.addObject("recentProducts", productService.getRecentProducts());
    return mav;
  }
}
