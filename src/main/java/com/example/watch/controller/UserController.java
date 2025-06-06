package com.example.watch.controller;

import com.example.watch.entity.User;
import com.example.watch.dto.UserDTO;
import com.example.watch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody UserDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Lưu ý cần phải chỉnh lại cái payload là password thay vì là passwordHash
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return service.update(id, dto);
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