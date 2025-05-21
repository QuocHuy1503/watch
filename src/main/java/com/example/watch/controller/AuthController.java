package com.example.watch.controller;

import com.example.watch.dto.UserLoginRequest;
import com.example.watch.dto.UserRegisterRequest;
import com.example.watch.entity.User;
import com.example.watch.repository.UserRepository;
import com.example.watch.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User user = new User();
        user.setName(req.name);
        user.setEmail(req.email);
        user.setPhone(req.phone);
        user.setGender(req.gender);
        user.setRole(req.role == null ? "customer" : req.role);
        user.setPasswordHash(passwordEncoder.encode(req.password));
//        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(req.password, user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("role", user.getRole());
        resp.put("name", user.getName());
        return ResponseEntity.ok(resp);
    }
}