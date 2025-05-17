package com.example.watch.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    @NotNull
    private Long userId;
    private List<OrderDetailDTO> details;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String status;
    private String paymentMethod;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime orderDate;
    private LocalDateTime updatedAt;
}
