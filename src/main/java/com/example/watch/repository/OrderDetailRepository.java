package com.example.watch.repository;

import com.example.watch.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query(value = "DELETE FROM order_details where order_id = :orderId" , nativeQuery = true)
    void deleteByOrderId(Long orderId);
}