package com.softworks.joongworld.report.repository;

import com.softworks.joongworld.report.model.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {

    int insertReport(ReportInsertParam param);

    Report findById(@Param("id") Long id);

    int updateResolution(@Param("id") Long id,
                         @Param("status") com.softworks.joongworld.consts.enums.ReportStatus status,
                         @Param("handlerId") Long handlerId,
                         @Param("resolutionType") com.softworks.joongworld.consts.enums.ReportResolutionType resolutionType,
                         @Param("handlerMemo") String handlerMemo,
                         @Param("processedAt") java.time.OffsetDateTime processedAt);
}
