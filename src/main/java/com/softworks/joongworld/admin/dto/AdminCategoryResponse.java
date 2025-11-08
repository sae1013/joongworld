package com.softworks.joongworld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCategoryResponse {
    private Integer id;
    private String name;
    private Integer displayOrder;
    private boolean active;
}
