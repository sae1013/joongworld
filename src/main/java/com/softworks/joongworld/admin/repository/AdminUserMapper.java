package com.softworks.joongworld.admin.repository;

import com.softworks.joongworld.admin.dto.AdminUserView;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminUserMapper {

    List<AdminUserView> findRecentUsers(@Param("limit") int limit);
}
