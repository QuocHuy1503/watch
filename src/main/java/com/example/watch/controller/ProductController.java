package com.example.watch.controller;

import com.example.watch.entity.Product;
import com.example.watch.entity.ProductFilter;
import com.example.watch.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return ResponseEntity.ok(service.findAllByPrice(minPrice, maxPrice));
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
            @PathVariable Long id,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return ResponseEntity.ok(
                service.findByCategoryAndPrice(id, minPrice, maxPrice)
        );
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Product>> getAllByCategories(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return ResponseEntity.ok(
                service.findAllByPriceInCategories(minPrice, maxPrice)
        );
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<List<Product>> findByBrand(
            @PathVariable Long id,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return ResponseEntity.ok(
                service.findByBrandAndPrice(id, minPrice, maxPrice)
        );
    }

    @GetMapping("/brands")
    public ResponseEntity<List<Product>> getAllByBrands(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice
    ) {
        return ResponseEntity.ok(
                service.findAllByPriceInBrands(minPrice, maxPrice)
        );
    }
//
//   @PostMapping("/header-search")
//   public ResponseEntity<List<Product>> headerSearch(@RequestBody ProductFilter filter) {
//       return ResponseEntity.ok(service.headerSearch(filter));
//   }

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
