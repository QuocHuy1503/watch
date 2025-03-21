package com.example.watch.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}