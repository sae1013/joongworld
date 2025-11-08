package com.softworks.joongworld.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryView {
    private Integer id;
    private String name;
    private Integer displayOrder;
}
