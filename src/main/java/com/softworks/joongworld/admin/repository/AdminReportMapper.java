package com.softworks.joongworld.admin.repository;

import com.softworks.joongworld.admin.dto.AdminProductReportView;
import com.softworks.joongworld.admin.dto.AdminUserReportView;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminReportMapper {

    List<AdminUserReportView> findRecentUserReports(@Param("limit") int limit);

    List<AdminProductReportView> findRecentProductReports(@Param("limit") int limit);
}
