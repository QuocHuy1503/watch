package com.example.watch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    @PositiveOrZero(message = "Parent category ID must be a positive number or zero")
    private Integer parentCategoryId; // 0 = no parent
}
