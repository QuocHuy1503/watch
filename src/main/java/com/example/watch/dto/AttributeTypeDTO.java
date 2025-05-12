package com.example.watch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AttributeTypeDTO {
    private Long attrTypeId;

    @NotBlank
    @Size(max = 100)
    private String name;
}
