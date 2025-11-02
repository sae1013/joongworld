package com.softworks.joongworld.category.repository;

import com.softworks.joongworld.product.dto.CategoryView;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<CategoryView> findAll();
}
