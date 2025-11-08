package com.softworks.joongworld.admin.controller.api;

import com.softworks.joongworld.admin.dto.AdminCategoryRequest;
import com.softworks.joongworld.admin.dto.AdminCategoryResponse;
import com.softworks.joongworld.admin.service.AdminCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class AdminCategoryApiController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<AdminCategoryResponse> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
        AdminCategoryResponse response = adminCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<AdminCategoryResponse> updateCategory(@PathVariable("categoryId") Integer categoryId,
                                                                @Valid @RequestBody AdminCategoryRequest request) {
        AdminCategoryResponse response = adminCategoryService.update(categoryId, request);
        return ResponseEntity.ok(response);
    }
}
