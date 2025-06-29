package com.example.watch.controller;

import com.example.watch.dto.CancelOrUpdateReceiverRequest;
import com.example.watch.dto.OrderDTO;
import com.example.watch.entity.Order;
import com.example.watch.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateStatus(@PathVariable Long id, @RequestBody String status) {
        OrderDTO updatedOrder = service.updateStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{id}/cancel-or-update-receiver")
    public ResponseEntity<OrderDTO> cancelOrUpdateReceiver(
            @PathVariable Long id,
            @RequestBody CancelOrUpdateReceiverRequest request
    ) {
        OrderDTO updated = service.cancelOrUpdateReceiver(id, request);
        return ResponseEntity.ok(updated);
    }
}