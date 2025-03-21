package com.example.watch.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> orderDetails;
}