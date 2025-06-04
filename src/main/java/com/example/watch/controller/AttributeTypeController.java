package com.example.watch.controller;

import com.example.watch.entity.AttributeType;
import com.example.watch.service.AttributeTypeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attribute-types")
public class AttributeTypeController {
    private final AttributeTypeService service;

    public AttributeTypeController(AttributeTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<AttributeType> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AttributeType getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<AttributeType> create(@Valid @RequestBody AttributeType type) {
        return ResponseEntity.ok(service.create(type));
    }

    @PutMapping("/{id}")
    public AttributeType update(@PathVariable Long id, @Valid @RequestBody AttributeType type) {
        return service.update(id, type);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}