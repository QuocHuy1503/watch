package com.example.watch.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private Long orderDetailId;
    @NotNull
    private Long productId;
    @Min(1) private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

}
