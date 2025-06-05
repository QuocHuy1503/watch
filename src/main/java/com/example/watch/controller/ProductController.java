package com.example.watch.controller;

import com.example.watch.entity.Product;
import com.example.watch.entity.ProductFilter;
import com.example.watch.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(
    ) {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(service.create(product));
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<List<Product>> findByCategory(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.getByCategory(id)
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Product>> getAllByCategories(
    ) {
        return ResponseEntity.ok(
                service.getAllByCategories()
        );
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<List<Product>> findByBrand(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.getByBrand(id)
        );
    }

    @GetMapping("/brands")
    public ResponseEntity<List<Product>> getAllByBrands(
    ) {
        return ResponseEntity.ok(
                service.getAllByBrands()
        );
    }
    @PostMapping("/tab-search")
    public ResponseEntity<List<Product>> search(@RequestBody ProductFilter filter) {
        return ResponseEntity.ok(service.filterProducts(filter));
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
