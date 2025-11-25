package com.softworks.joongworld.report.model;

import com.softworks.joongworld.consts.enums.ReportResolutionType;
import com.softworks.joongworld.consts.enums.ReportStatus;
import com.softworks.joongworld.consts.enums.ReportTargetType;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Report {
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
    private OffsetDateTime createdAt;
    private OffsetDateTime processedAt;
}
