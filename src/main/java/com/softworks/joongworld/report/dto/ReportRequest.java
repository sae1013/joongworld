package com.softworks.joongworld.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotBlank(message = "신고 사유를 선택해 주세요.")
    @Size(max = 50, message = "신고 사유 코드는 50자를 넘을 수 없습니다.")
    private String reasonCode;

    @NotBlank(message = "신고 내용을 입력해 주세요.")
    @Size(max = 1000, message = "신고 내용은 1000자 이하로 작성해 주세요.")
    private String description;
}
