package com.example.watch.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Entity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Setter
@Getter
public class ProductFilter {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<Long> brandIds;
    private List<Long> categoryIds;
    private List<AttributeFilter> attributes;

    // getters + setters
    @Getter
    @Setter
    public static class AttributeFilter {
        private Long attrTypeId;
        private List<String> values;
        // getters + setters
    }
}