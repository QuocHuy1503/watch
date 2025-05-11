package com.example.watch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "discounts", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    @Size(max = 10)
    private String type;          // e.g. "percent" or "fixed"

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal value;

    @NotNull
    private LocalDate validFrom;

    @NotNull
    private LocalDate validUntil;

    private Integer maxUses;

    private Integer usesCount = 0;

    private Boolean active = true;

    // getters and setters
}