package com.example.watch.entity.dto;

import lombok.*;

import java.math.*;
import java.time.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal amount;
    private LocalDate validUntil;
    private Integer maxUses;
    private LocalDateTime createdAt;

    // getters and setters
}