package com.example.watch.repository;

import com.example.watch.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Override
    @Query(value = "SELECT * FROM orders", nativeQuery = true)
    List<Order> findAll();

    @Query(value = " DELETE FROM orders WHERE user_id = :userId", nativeQuery = true)
    Order deleteByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * from orders where user_id = :userId " , nativeQuery = true)
    Order findByUserId(@Param("userId") Long userId);
}