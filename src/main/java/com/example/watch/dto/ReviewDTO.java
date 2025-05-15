package com.example.watch.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;

    @NotNull
    private Long userId;
    @NotNull private Long productId;

    @NotNull @Min(1) @Max(5)
    private Short rating;

    private String comment;
    private LocalDateTime createdAt;
}
