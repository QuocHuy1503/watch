package com.example.watch.service;

import com.example.watch.entity.*;
import com.example.watch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private OrderRepository orderRepo;
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    @Autowired
    private UserRepository userRepo;

    // 1. Phần trăm loại sản phẩm trong danh mục
    public List<Map<String, Object>> getCategoryPercentages() {
        long total = productRepo.count();
        List<Category> categories = categoryRepo.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category cat : categories) {
            long count = productRepo.countByCategory(cat);
            double percent = total == 0 ? 0 : (count * 100.0 / total);
            Map<String, Object> item = new HashMap<>();
            item.put("category", cat.getName());
            item.put("count", count);
            item.put("percentage", Math.round(percent * 100.0) / 100.0);
            result.add(item);
        }
        return result;
    }

    // Helper: get start/end of day
    private LocalDateTime[] getDayRange(LocalDate day) {
        return new LocalDateTime[]{
                day.atStartOfDay(),
                day.atTime(LocalTime.MAX)
        };
    }

    // 2. Dashboard hôm nay và so với hôm qua
    public Map<String, Object> getTodayDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDateTime[] todayRange = getDayRange(today);
        LocalDateTime[] yestRange = getDayRange(yesterday);

        // Doanh thu
        BigDecimal revenueToday = orderRepo.sumTotalByOrderDateBetween(todayRange[0], todayRange[1]);
        BigDecimal revenueYest = orderRepo.sumTotalByOrderDateBetween(yestRange[0], yestRange[1]);

        // Đơn hàng
        long ordersToday = orderRepo.countByOrderDateBetween(todayRange[0], todayRange[1]);
        long ordersYest = orderRepo.countByOrderDateBetween(yestRange[0], yestRange[1]);

        // Sản phẩm bán ra
        Long soldToday = orderDetailRepo.sumQuantityByOrderDateBetween(todayRange[0], todayRange[1]);
        Long soldYest = orderDetailRepo.sumQuantityByOrderDateBetween(yestRange[0], yestRange[1]);
        if (soldToday == null) soldToday = 0L;
        if (soldYest == null) soldYest = 0L;

        // Khách hàng mới
        long userToday = userRepo.countByCreatedAtBetween(todayRange[0], todayRange[1]);
        long userYest = userRepo.countByCreatedAtBetween(yestRange[0], yestRange[1]);

        // % tăng giảm
        double revPercent = percentCompare(revenueToday, revenueYest);
        double orderPercent = percentCompare(ordersToday, ordersYest);
        double soldPercent = percentCompare(soldToday, soldYest);
        double userPercent = percentCompare(userToday, userYest);

        Map<String, Object> result = new HashMap<>();
        result.put("revenue", Map.of(
                "today", revenueToday == null ? 0 : revenueToday,
                "yesterday", revenueYest == null ? 0 : revenueYest,
                "percent", revPercent));
        result.put("orders", Map.of(
                "today", ordersToday,
                "yesterday", ordersYest,
                "percent", orderPercent));
        result.put("productsSold", Map.of(
                "today", soldToday,
                "yesterday", soldYest,
                "percent", soldPercent));
        result.put("newUsers", Map.of(
                "today", userToday,
                "yesterday", userYest,
                "percent", userPercent));
        return result;
    }

    private double percentCompare(Number today, Number yest) {
        double t = today == null ? 0 : today.doubleValue();
        double y = yest == null ? 0 : yest.doubleValue();
        if (y == 0) return t > 0 ? 100 : 0;
        return Math.round(((t - y) / y) * 10000.0) / 100.0;
    }

    // 3. 4 đơn hàng gần nhất
    public List<Map<String, Object>> getRecentOrders(int limit) {
        List<Order> orders = orderRepo.findTop4ByOrderByOrderDateDesc();
        return orders.stream().map(order -> Map.of(
                "orderId", order.getOrderId(),
                "receiverName", order.getReceiverName(),
                "receiverPhone", order.getReceiverPhone(),
                "shippingAddress", order.getShippingAddress(),
                "total", order.getTotal(),
                "orderDate", order.getOrderDate(),
                "status", order.getStatus(),
                "user", Map.of(
                        "userId", order.getUser().getUserId(),
                        "name", order.getUser().getName(),
                        "email", order.getUser().getEmail()
                )
        )).collect(Collectors.toList());
    }
}