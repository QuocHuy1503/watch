package com.example.watch.repository;

import com.example.watch.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_details where order_id = :orderId" , nativeQuery = true)
    void deleteByOrderId(Long orderId);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od JOIN od.order o WHERE o.orderDate BETWEEN :start AND :end")
    Long sumQuantityByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}