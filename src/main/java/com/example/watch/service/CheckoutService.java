package com.example.watch.service;

import com.example.watch.dto.CheckoutRequest;
import com.example.watch.entity.*;
import com.example.watch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CheckoutService {
    private final CartRepository cartRepo;
    private final DiscountRepository discountRepo;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final CartItemRepository cartItemRepo;

    public CheckoutService(CartRepository cartRepo,
                           DiscountRepository discountRepo,
                           OrderRepository orderRepo,
                           OrderDetailRepository detailRepo,
                           CartItemRepository cartItemRepo) {
        this.cartRepo = cartRepo;
        this.discountRepo = discountRepo;
        this.orderRepo = orderRepo;
        this.detailRepo = detailRepo;
        this.cartItemRepo = cartItemRepo;
    }

    // Checkout đã xong nhưng để checkout cần token của người dùng, thông tin receiver
    @Transactional
    public Order checkout(User user, CheckoutRequest req) {
        List<Cart> carts = cartRepo.findAllCartsByUserId(user.getUserId());
        Cart cart = carts.get(0); // or apply other selection logic

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Calculate subtotal
        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tổng tiền = subtotal (vì không có discount)
        BigDecimal total = subtotal;

        // Create and save Order
        Order order = new Order();
        order.setUser(user);
        order.setSubtotal(subtotal);
        order.setTotal(total);
        order.setPaymentMethod(req.getPaymentMethod());
        order.setShippingAddress(req.getShippingAddress());
        order.setReceiverName(req.getReceiverName());
        order.setReceiverPhone(req.getReceiverPhone());
        Order savedOrder = orderRepo.save(order);

        // Save OrderDetails
        for (CartItem item : cart.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getProduct().getPrice());
            detailRepo.save(detail);
        }

        // Clear cart
        cartItemRepo.deleteAll(cart.getItems());

        return savedOrder;
    }
}