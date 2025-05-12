package com.example.watch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AttributeValueDTO {
    private Long attrValueId;

    @NotNull
    private Long productId;

    @NotNull
    private Long attrTypeId;

    @NotBlank
    @Size(max = 255)
    private String value;
}
