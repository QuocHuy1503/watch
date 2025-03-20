// CategoryResponse.java
package com.example.watch.dto;

import lombok.Data;

@Data
public class CategoryResponse {
    private Integer categoryId;
    private String name;
    private Integer parentCategoryId;
    private boolean hasChildren;
}