package com.example.watch.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductDTO {
    private Long productId;

    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;

    @Size(max = 50)
    private String sku;
    private Boolean active;
    private String status;

    private Integer soldQuantity;

    private Integer remainQuantity;

    private Long brandId;

    private Long categoryId;

    private List<AttributeValueDTO> attributeValues;
    // getters and setters
}