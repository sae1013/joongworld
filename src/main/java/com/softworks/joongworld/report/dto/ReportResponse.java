package com.softworks.joongworld.report.dto;

import com.softworks.joongworld.consts.enums.ReportStatus;
import com.softworks.joongworld.consts.enums.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReportResponse {
    private final Long id;
    private final ReportTargetType targetType;
    private final Long targetId;
    private final ReportStatus status;
    private final String reasonCode;
    private final String description;
}
