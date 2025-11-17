package com.softworks.joongworld.category.controller;

import com.softworks.joongworld.category.service.CategoryService;
import com.softworks.joongworld.product.dto.CategoryView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * 전체 카테고리 조회 API
   *
   * @return
   */
  @GetMapping(value = "/api/categories")
  public ResponseEntity<List<CategoryView>> getCategories() {
    return ResponseEntity.ok(categoryService.getAllCategories());
  }
}
