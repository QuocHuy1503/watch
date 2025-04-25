package com.example.watch.entity.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

}