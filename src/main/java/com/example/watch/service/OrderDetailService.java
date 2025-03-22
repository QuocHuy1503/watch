package com.example.watch.service;

import com.example.watch.dto.*;
import com.example.watch.entity.*;
import com.example.watch.exception.*;
import com.example.watch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderDetailService(
            OrderDetailRepository orderDetailRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Lấy tất cả chi tiết đơn hàng theo orderId
    public List<OrderDetailResponse> getOrderDetailsByOrderId(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return orderDetails.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Thêm chi tiết đơn hàng
    @Transactional
    public OrderDetailResponse createOrderDetail(OrderDetailRequest request) throws DuplicateResourceException {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Kiểm tra chi tiết đơn hàng đã tồn tại
        if (orderDetailRepository.existsByOrder_OrderIdAndProduct_ProductId(
                request.getOrderId(),
                request.getProductId()
        )) {
            throw new DuplicateResourceException("Order detail already exists");
        }

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPrice(request.getPrice());

        OrderDetail savedDetail = (OrderDetail) orderDetailRepository.save(orderDetail);
        return mapToResponse(savedDetail);
    }

    // Cập nhật chi tiết đơn hàng
    @Transactional
    public OrderDetailResponse updateOrderDetail(
            Long orderId,
            Long productId,
            OrderDetailRequest request
    ) {
        OrderDetail orderDetail = null;
        try {
            orderDetail = (OrderDetail) orderDetailRepository.findById(new OrderDetailId(orderId, productId))
                    .orElseThrow(() -> new ResourceNotFoundException("Order detail not found"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // Cập nhật số lượng và giá
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setPrice(request.getPrice());

        OrderDetail updatedDetail = (OrderDetail) orderDetailRepository.save(orderDetail);
        return mapToResponse(updatedDetail);
    }

    // Xóa chi tiết đơn hàng
    @Transactional
    public void deleteOrderDetail(Long orderId, Long productId) {
        if (!orderDetailRepository.existsById(new OrderDetailId(orderId, productId))) {
            throw new ResourceNotFoundException("Order detail not found");
        }
        orderDetailRepository.deleteById(new OrderDetailId(orderId, productId));
    }

    // Convert Entity → Response DTO
    private OrderDetailResponse mapToResponse(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(orderDetail.getOrder().getOrderId());
        response.setProductId(orderDetail.getProduct().getProductId());
        response.setQuantity(orderDetail.getQuantity());
        response.setPrice(orderDetail.getPrice());
        return response;
    }
}