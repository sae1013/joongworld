package com.softworks.joongworld.admin.dto;

import com.softworks.joongworld.consts.enums.ReportResolutionType;
import com.softworks.joongworld.consts.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResolutionRequest {

    @NotNull
    private ReportStatus reportStatus;

    /**
     * USER 또는 PRODUCT 대상에 적용할 상태 값 (enum name 문자열).
     */
    private String targetStatus;

    private ReportResolutionType resolutionType;

    @Size(max = 1000)
    private String memo;
}
