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

    // 1. Tổng doanh thu hôm nay và hôm qua
    @Query(value = """
        SELECT 
            COALESCE(SUM(CASE WHEN DATE(order_date) = CURRENT_DATE THEN total ELSE 0 END), 0) AS revenueToday,
            COALESCE(SUM(CASE WHEN DATE(order_date) = CURRENT_DATE - INTERVAL '1 day' THEN total ELSE 0 END), 0) AS revenueYesterday
        FROM orders
        WHERE status IN ('completed', 'paid', 'delivered')
    """, nativeQuery = true)
    Object getTodayAndYesterdayRevenue();

    // 2. Số đơn khách đặt hôm nay và hôm qua
    @Query(value = """
        SELECT 
            COUNT(CASE WHEN DATE(order_date) = CURRENT_DATE THEN 1 END) AS ordersToday,
            COUNT(CASE WHEN DATE(order_date) = CURRENT_DATE - INTERVAL '1 day' THEN 1 END) AS ordersYesterday
        FROM orders
    """, nativeQuery = true)
    Object getTodayAndYesterdayOrderCount();

    // 3. Số khách hàng đăng ký mới hôm nay và hôm qua
    @Query(value = """
        SELECT 
            COUNT(CASE WHEN DATE(created_at) = CURRENT_DATE THEN 1 END) AS usersToday,
            COUNT(CASE WHEN DATE(created_at) = CURRENT_DATE - INTERVAL '1 day' THEN 1 END) AS usersYesterday
        FROM users
    """, nativeQuery = true)
    Object getTodayAndYesterdayUserCount();

    // 4. Số lượng đơn hàng theo từng trạng thái hôm nay
    @Query(value = """
        SELECT 
            status, COUNT(*) AS countToday
        FROM orders
        WHERE DATE(order_date) = CURRENT_DATE
        GROUP BY status
    """, nativeQuery = true)
    java.util.List<Object[]> getTodayOrderStatusCount();
}