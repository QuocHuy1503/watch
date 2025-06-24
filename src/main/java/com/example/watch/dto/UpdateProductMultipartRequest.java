package com.example.watch.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductMultipartRequest {
    @NotBlank
    @Size(max = 150)
    private String name;

    private String description;

    @NotNull @DecimalMin("0.00")
    private BigDecimal price;

    @Size(max = 50)
    private String sku;
    private Boolean active;
    private String status;
    private Integer soldQuantity;
    private Integer remainQuantity;

    @NotNull
    private Long brandId;

    private Long categoryId;

    /** Ảnh mới (MultipartFile), không bắt buộc */
    private List<MultipartFile> images;

    /** ID các ảnh cũ muốn giữ lại, không bắt buộc */
    private List<Long> existingImageIds;

    /** Nếu update chỉ đổi ảnh chính, có thể gửi primaryImageId thay vì index */
    private Long primaryImageId;

    /** Hoặc: bạn vẫn có thể dùng primaryImageIndex nếu muốn */
    private Integer primaryImageIndex;
}
