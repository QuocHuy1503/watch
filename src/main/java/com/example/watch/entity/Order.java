package com.example.watch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> details;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;

    @Column(name = "discount_id")
    private Integer discountId;

    @NotBlank
    private String status = "pending";
    private String paymentMethod;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;

    @Column(nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
