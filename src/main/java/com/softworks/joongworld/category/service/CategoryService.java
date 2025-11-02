package com.softworks.joongworld.category.service;

import com.softworks.joongworld.category.repository.CategoryMapper;
import com.softworks.joongworld.product.dto.CategoryView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryView> getAllCategories() {
        return categoryMapper.findAll();
    }
}
