package com.example.watch.repository;

import com.example.watch.entity.Order;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy đơn hàng chưa xóa theo ID
    @Query("SELECT o FROM Order o WHERE o.orderId = ?1 AND o.deleted = false")
    Optional<Order> findActiveById(Long orderId);

    // Lấy tất cả đơn hàng chưa xóa
    @Query("SELECT o FROM Order o WHERE o.deleted = false")
    List<Order> findAllActiveOrders();

    // Xóa mềm
    @Modifying
    @Query("UPDATE Order o SET o.deleted = true WHERE o.orderId = ?1")
    void softDelete(Long orderId);
}