package com.example.watch.controller;

import com.example.watch.entity.Product;
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
    public List<Product> getAll() {
        return service.findAll();
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
    public ResponseEntity<List<Product>> findByCategory(@PathVariable Long id) {
        List<Product> products = service.getByCategory(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Product>> getAllByCategories() {
        List<Product> products = service.getAllByCategories();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<List<Product>> findByBrand(@PathVariable Long id) {
        List<Product> products = service.getByBrand(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/brands")
    public ResponseEntity<List<Product>> getAllByBrands() {
        List<Product> products = service.getAllByBrands();
        return ResponseEntity.ok(products);
    }
}
