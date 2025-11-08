package com.softworks.joongworld.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryRequest {

    @NotBlank(message = "카테고리명을 입력해 주세요.")
    private String name;

    @NotNull(message = "표시 순서를 입력해 주세요.")
    @Min(value = 1, message = "표시 순서는 1 이상의 숫자여야 합니다.")
    private Integer displayOrder;

    @NotNull(message = "상태를 선택해 주세요.")
    private Boolean active;
}
