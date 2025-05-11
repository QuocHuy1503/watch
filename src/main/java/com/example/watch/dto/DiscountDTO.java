package com.example.watch.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class DiscountDTO {
    private Long discountId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 10)
    private String type;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal value;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validUntil;

    private Integer maxUses;

    private Boolean active;

    // getters and setters
}