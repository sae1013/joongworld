package com.softworks.joongworld.admin.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserReportView {
    private Long id;
    private String reporter;
    private String target;
    private String reason;
    private String description;
    private String status;
    private String targetStatus;
    private OffsetDateTime reportedAt;
    private String handlerNickname;
    private OffsetDateTime processedAt;
}
