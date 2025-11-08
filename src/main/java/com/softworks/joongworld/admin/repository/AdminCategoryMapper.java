package com.softworks.joongworld.admin.repository;

import com.softworks.joongworld.admin.dto.AdminCategoryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCategoryMapper {

    List<AdminCategoryResponse> findAll();

    AdminCategoryResponse findById(@Param("id") Integer id);

    AdminCategoryResponse findByName(@Param("name") String name);

    boolean existsByName(@Param("name") String name, @Param("excludeId") Integer excludeId);

    int insertCategory(@Param("name") String name,
                       @Param("displayOrder") Integer displayOrder,
                       @Param("active") boolean active);

    int updateCategory(@Param("id") Integer id,
                       @Param("name") String name,
                       @Param("displayOrder") Integer displayOrder,
                       @Param("active") boolean active);
}
