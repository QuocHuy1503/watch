package com.example.watch.controller;

import com.example.watch.dto.*;
import com.example.watch.exception.DuplicateResourceException;
import com.example.watch.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/details")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    // Lấy tất cả chi tiết đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetails(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByOrderId(orderId));
    }

    // Thêm chi tiết đơn hàng
    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrderDetail(
            @PathVariable Long orderId,
            @RequestBody OrderDetailRequest request
    ) throws DuplicateResourceException {
        request.setOrderId(orderId); // Đảm bảo orderId trong request khớp với URL
        return new ResponseEntity<>(orderDetailService.createOrderDetail(request), HttpStatus.CREATED);
    }

    // Cập nhật chi tiết đơn hàng
    @PutMapping("/{productId}")
    public ResponseEntity<OrderDetailResponse> updateOrderDetail(
            @PathVariable Long orderId,
            @PathVariable Long productId,
            @RequestBody OrderDetailRequest request
    ) {
        request.setOrderId(orderId);
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(orderId, productId, request));
    }

    // Xóa chi tiết đơn hàng
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteOrderDetail(
            @PathVariable Long orderId,
            @PathVariable Long productId
    ) {
        orderDetailService.deleteOrderDetail(orderId, productId);
        return ResponseEntity.noContent().build();
    }
}