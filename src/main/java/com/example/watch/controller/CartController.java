package com.example.watch.controller;

import com.example.watch.dto.CartDTO;
import com.example.watch.dto.CartItemDTO;
import com.example.watch.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/cart")
public class CartController {
    private final CartService service;
    public CartController(CartService service) { this.service = service; }

    @GetMapping public CartDTO getCart(@PathVariable Long userId) {
        return service.getCart(userId);
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addItem(@PathVariable Long userId,
                                               @Valid @RequestBody CartItemDTO dto) {
        return ResponseEntity.ok(service.addItem(userId, dto));
    }

    @DeleteMapping("/{itemId}") public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        service.removeItem(itemId); return ResponseEntity.noContent().build();
    }

    @DeleteMapping public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        service.clearCart(userId); return ResponseEntity.noContent().build();
    }
}