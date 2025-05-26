package com.example.watch.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data                // sinh getters/setters, toString, equals/hashCode
@NoArgsConstructor   // sinh constructor không tham số
@AllArgsConstructor  // sinh constructor full-args

public class AttributeFilterDTO {
    private Long attrTypeId;
    private String typeName;
    private List<String> values;

    // getters/setters
}
