package com.example.watch.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class ProductImageDTO {
    private Long imageId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "Image URL is required")
    @Size(max = 255)
    private String imageUrl;

    private Boolean isPrimary;

    // getters and setters
}