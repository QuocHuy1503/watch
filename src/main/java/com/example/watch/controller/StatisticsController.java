package com.example.watch.controller;

import com.example.watch.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // 1. Phần trăm loại sản phẩm
    @GetMapping("/category-percentages")
    public List<Map<String, Object>> getCategoryPercentages() {
        return statisticsService.getCategoryPercentages();
    }

    // 2. Dashboard hôm nay
    @GetMapping("/dashboard")
    public Map<String, Object> getTodayDashboard() {
        return statisticsService.getTodayDashboard();
    }

    // 3. 4 đơn hàng mới nhất
    @GetMapping("/recent-orders")
    public List<Map<String, Object>> getRecentOrders() {
        return statisticsService.getRecentOrders(4);
    }
}