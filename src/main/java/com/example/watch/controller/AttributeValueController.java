package com.example.watch.controller;

import com.example.watch.entity.AttributeValue;
import com.example.watch.dto.AttributeValueDTO;
import com.example.watch.service.AttributeValueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/attribute-values")
public class AttributeValueController {
    private final AttributeValueService service;

    public AttributeValueController(AttributeValueService service) {
        this.service = service;
    }

    @GetMapping
    public List<AttributeValue> getAll(@PathVariable Long productId) {
        return service.findByProduct(productId);
    }

    @GetMapping("/{id}")
    public AttributeValue getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<AttributeValue> create(@PathVariable Long productId,
                                                 @Valid @RequestBody AttributeValueDTO dto) {
        dto.setProductId(productId);
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public AttributeValue update(@PathVariable Long id,
                                 @Valid @RequestBody AttributeValueDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}