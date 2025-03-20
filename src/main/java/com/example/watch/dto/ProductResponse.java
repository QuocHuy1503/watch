package com.example.watch.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String brand;
    private String sku;
    private Integer categoryId;
    private LocalDateTime createdAt;
}