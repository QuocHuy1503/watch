package com.example.watch.service;

import com.example.watch.dto.*;
import com.example.watch.entity.*;
import com.example.watch.exception.*;
import com.example.watch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Lấy tất cả đơn hàng
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllActiveOrders().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Lấy đơn hàng theo ID
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(order);
    }

    // Tạo đơn hàng
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(request.getStatus());
        order.setPaymentMethod(request.getPaymentMethod());

        // Thêm chi tiết đơn hàng
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailRequest.getQuantity());
            orderDetail.setPrice(detailRequest.getPrice());
            orderDetail.setOrder(order);
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    // Cập nhật đơn hàng
    @Transactional
    public OrderResponse updateOrder(Long orderId, OrderRequest request) {
        Order order = orderRepository.findActiveById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        order.setUser(user);
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(request.getStatus());
        order.setPaymentMethod(request.getPaymentMethod());

        // Cập nhật chi tiết đơn hàng
        order.getOrderDetails().clear();
        for (OrderDetailRequest detailRequest : request.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailRequest.getQuantity());
            orderDetail.setPrice(detailRequest.getPrice());
            orderDetail.setOrder(order);
            order.getOrderDetails().add(orderDetail);
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    // Xóa mềm
    @Transactional
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found");
        }
        orderRepository.softDelete(orderId);
    }

    // Convert Entity → Response DTO
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUser().getUserId());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setCreatedAt(order.getCreatedAt());

        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(this::mapToDetailResponse)
                .toList();
        response.setOrderDetails(detailResponses);

        return response;
    }

    // Convert OrderDetail Entity → Response DTO
    private OrderDetailResponse mapToDetailResponse(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setProductId(orderDetail.getProduct().getProductId());
        response.setQuantity(orderDetail.getQuantity());
        response.setPrice(orderDetail.getPrice());
        return response;
    }
}