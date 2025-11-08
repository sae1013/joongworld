package com.softworks.joongworld.admin.service;

import com.softworks.joongworld.admin.dto.AdminCategoryRequest;
import com.softworks.joongworld.admin.dto.AdminCategoryResponse;
import com.softworks.joongworld.admin.repository.AdminCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final AdminCategoryMapper adminCategoryMapper;

    @Transactional(readOnly = true)
    public List<AdminCategoryResponse> getAll() {
        return adminCategoryMapper.findAll();
    }

    @Transactional
    public AdminCategoryResponse create(AdminCategoryRequest request) {
        System.out.println(request);
        validateUniqueName(request.getName(), null);
        int inserted = adminCategoryMapper.insertCategory(
                request.getName().trim(),
                request.getDisplayOrder(),
                Boolean.TRUE.equals(request.getActive()));
        if (inserted != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 생성 중 문제가 발생했습니다.");
        }
        AdminCategoryResponse created = adminCategoryMapper.findByName(request.getName());
        if (created == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "생성된 카테고리를 찾지 못했습니다.");
        }
        return created;
    }

    @Transactional
    public AdminCategoryResponse update(Integer categoryId, AdminCategoryRequest request) {
        if (categoryId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리 ID가 필요합니다.");
        }
        ensureCategoryExists(categoryId);
        validateUniqueName(request.getName(), categoryId);
        int updated = adminCategoryMapper.updateCategory(
                categoryId,
                request.getName().trim(),
                request.getDisplayOrder(),
                Boolean.TRUE.equals(request.getActive()));
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 수정에 실패했습니다.");
        }
        return adminCategoryMapper.findById(categoryId);
    }

    private void ensureCategoryExists(Integer id) {
        if (adminCategoryMapper.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.");
        }
    }

    private void validateUniqueName(String name, Integer excludeId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리명을 입력해 주세요.");
        }
        if (adminCategoryMapper.existsByName(name.trim(), excludeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 카테고리명입니다.");
        }
    }
}
