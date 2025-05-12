package com.example.watch.controller;
import com.example.watch.entity.ProductImage;
import com.example.watch.dto.ProductImageDTO;
import com.example.watch.service.ProductImageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {
    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductImage> getAll(@PathVariable Long productId) {
        return service.findAllByProduct(productId);
    }

    @GetMapping("/{id}")
    public ProductImage getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProductImage> create(@PathVariable Long productId,
                                               @Valid @RequestBody ProductImageDTO dto) {
        dto.setProductId(productId);
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ProductImage update(@PathVariable Long id,
                               @PathVariable Long productId,
                               @Valid @RequestBody ProductImageDTO dto) {
        dto.setProductId(productId);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}