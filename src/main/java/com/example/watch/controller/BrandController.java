package com.example.watch.controller;
import com.example.watch.entity.Brand;
import com.example.watch.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final BrandService service;

    public BrandController(BrandService service) {
        this.service = service;
    }

    @GetMapping
    public List<Brand> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Brand getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<Brand> create(@Valid @RequestBody Brand brand) {
        Brand created = service.create(brand);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public Brand update(@PathVariable Long id, @Valid @RequestBody Brand brand) {
        return service.update(id, brand);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}