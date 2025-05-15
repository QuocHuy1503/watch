package com.example.watch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {

    private Long itemId;
    @NotNull private Long productId;
    @Min(1) private Integer quantity;

}
