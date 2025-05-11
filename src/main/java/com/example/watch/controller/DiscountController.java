package com.example.watch.controller;

import com.example.watch.entity.Discount;
import com.example.watch.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {
    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @GetMapping
    public List<Discount> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Discount getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Discount> create(@Valid @RequestBody Discount discount) {
        return ResponseEntity.ok(service.create(discount));
    }

    @PutMapping("/{id}")
    public Discount update(@PathVariable Long id, @Valid @RequestBody Discount discount) {
        return service.update(id, discount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}