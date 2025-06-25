package com.example.watch.service;
import com.example.watch.dto.CancelOrUpdateReceiverRequest;
import com.example.watch.dto.OrderDTO;
import com.example.watch.dto.OrderDetailDTO;
import com.example.watch.entity.Order;
import com.example.watch.entity.OrderDetail;
import com.example.watch.entity.Product;
import com.example.watch.entity.User;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.OrderRepository;
import com.example.watch.repository.ProductRepository;
import com.example.watch.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        UserRepository userRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public List<OrderDTO> findAll() {
        return orderRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderDTO findById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        return toDto(order);
    }

    public OrderDTO create(OrderDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderDetail> details = dto.getDetails().stream().map(d -> {
            Product p = productRepo.findById(d.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            OrderDetail od = new OrderDetail();
            od.setOrder(order);
            od.setProduct(p);
            od.setQuantity(d.getQuantity());
            od.setUnitPrice(p.getPrice());
            return od;
        }).collect(Collectors.toList());

        order.setDetails(details);

        BigDecimal subtotal = details.stream()
                .map(OrderDetail::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);

        Order saved = orderRepo.save(order);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id " + id);
        }
        orderRepo.deleteById(id);
    }

    private OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setStatus(order.getStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setTotal(order.getTotal());
        dto.setOrderDate(order.getOrderDate());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setShippingAddress(order.getShippingAddress());

        List<OrderDetailDTO> details = order.getDetails().stream().map(od -> {
            OrderDetailDTO dd = new OrderDetailDTO();
            dd.setOrderDetailId(od.getOrderDetailId());
            dd.setProductId(od.getProduct().getProductId());
            dd.setQuantity(od.getQuantity());
            dd.setUnitPrice(od.getUnitPrice());
            dd.setLineTotal(od.getLineTotal());
            return dd;
        }).collect(Collectors.toList());
        dto.setDetails(details);

        return dto;
    }

    public OrderDTO updateStatus(Long id, String status) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        order.setStatus(status);
        order.setUpdatedAt(java.time.LocalDateTime.now());
        Order saved = orderRepo.save(order);
        return toDto(saved);
    }

    public OrderDTO cancelOrUpdateReceiver(Long id, CancelOrUpdateReceiverRequest req) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
        if (Boolean.TRUE.equals(req.getCancel())) {
            if (!"pending".equalsIgnoreCase(order.getStatus())) {
                throw new IllegalStateException("Only pending orders can be cancelled.");
            }
            order.setStatus("cancelled");
        }
        if (req.getReceiverName() != null && !req.getReceiverName().isBlank()) {
            order.setReceiverName(req.getReceiverName());
        }
        if (req.getReceiverPhone() != null && !req.getReceiverPhone().isBlank()) {
            order.setReceiverPhone(req.getReceiverPhone());
        }
        if (req.getReceiverAddress() != null && !req.getReceiverAddress().isBlank()) {
            order.setShippingAddress(req.getReceiverAddress());
        }
        order.setUpdatedAt(java.time.LocalDateTime.now());
        return toDto(orderRepo.save(order));
    }
}