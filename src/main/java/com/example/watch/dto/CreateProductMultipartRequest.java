// com/example/watch/dto/CreateProductMultipartRequest.java
package com.example.watch.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductMultipartRequest {
    @NotBlank @Size(max = 150)
    private String name;

    private String description;

    @NotNull @DecimalMin("0.00")
    private BigDecimal price;

    @Size(max = 50)
    private String sku;

    private String status;
    private Integer soldQuantity;
    private Integer remainQuantity;

    @NotNull
    private Long brandId;
    private Long categoryId;

    @NotEmpty(message = "Cần ít nhất một file ảnh")
    private List<MultipartFile> images;

    /** FE có thể chỉ định index file primary (0-based). Nếu null, mặc định file đầu tiên */
    private Integer primaryImageIndex;
}
