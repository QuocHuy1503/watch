// CategoryRequest.java
package com.example.watch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String name;

    private Integer parentCategoryId; // ID của danh mục cha (optional)
}

