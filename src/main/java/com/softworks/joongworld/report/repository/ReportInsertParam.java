package com.softworks.joongworld.report.repository;

import com.softworks.joongworld.consts.enums.ReportResolutionType;
import com.softworks.joongworld.consts.enums.ReportStatus;
import com.softworks.joongworld.consts.enums.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportInsertParam {
    private Long id;
    private Long reporterId;
    private Long reportedUserId;
    private Long reportedProductId;
    private ReportTargetType targetType;
    private String reasonCode;
    private String description;
    private Long handlerId;
    private ReportStatus status;
    private ReportResolutionType resolutionType;
    private String handlerMemo;
}
