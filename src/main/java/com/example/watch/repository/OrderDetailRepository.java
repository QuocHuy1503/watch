package com.example.watch.repository;

import com.example.watch.entity.OrderDetail;
import com.example.watch.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderDetailRepository<OrderDetailId> extends JpaRepository<OrderDetail, OrderDetailId> {
    // Lấy tất cả chi tiết đơn hàng theo orderId
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = ?1")
    List<OrderDetail> findByOrderId(Long orderId);

    // Kiểm tra tồn tại chi tiết đơn hàng
    boolean existsByOrder_OrderIdAndProduct_ProductId(Long orderId, Long productId);
}