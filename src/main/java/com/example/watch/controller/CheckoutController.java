package com.example.watch.controller;

import com.example.watch.dto.CheckoutRequest;
import com.example.watch.entity.Order;
import com.example.watch.entity.User;
import com.example.watch.repository.UserRepository;
import com.example.watch.service.CheckoutService;
import com.example.watch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final UserService userService;
    private final UserRepository userRepository;
    public CheckoutController(CheckoutService checkoutService, UserService userService, UserRepository userRepository) {
        this.checkoutService = checkoutService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Order> checkout(@RequestBody @Valid CheckoutRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Order order = checkoutService.checkout(user, req);
        return ResponseEntity.ok(order);
    }
}