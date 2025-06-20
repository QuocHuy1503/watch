package com.example.watch.controller;

import com.example.watch.dto.UserForgotPasswordRequest;
import com.example.watch.dto.UserLoginRequest;
import com.example.watch.dto.UserRegisterRequest;
import com.example.watch.dto.UserResetPasswordRequest;
import com.example.watch.entity.User;
import com.example.watch.repository.UserRepository;
import com.example.watch.security.JwtUtil;
import com.example.watch.security.TokenBlacklist;
import com.example.watch.service.EmailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.from:no-reply@example.com}")
    private String mailFrom;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Value("${frontend.url}") private String frontendUrl;


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
        user.setAddress(req.address);
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
        if ("inactive".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User account is locked"));
        }
        String token = jwtUtil.generateToken(user);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("role", user.getRole());
        resp.put("name", user.getName());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized"));
        }
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
        User user = userOpt.get();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserForgotPasswordRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.email);
        if (userOpt.isEmpty()) {
            // Không tiết lộ email tồn tại/vắng mặt
            return ResponseEntity.ok("If this email exists, a reset link has been sent.");
        }
        User user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        // Tạo link reset (giả sử FE có trang /reset-password?token=...)
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        String subject = "Password Reset Request";
        String text = "Hello " + user.getName() + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Click the link below to set a new password:\n"
                + resetLink + "\n\n"
                + "If you didn't request this, please ignore this email.";

        emailService.sendSimpleMessage(user.getEmail(), subject, text);

        return ResponseEntity.ok("If this email exists, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody UserResetPasswordRequest req) {
        Optional<User> userOpt = userRepository.findByResetToken(req.token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired reset token");
        }
        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(req.newPassword));
        user.setResetToken(null); // Xóa reset token sau khi đổi mật khẩu
        userRepository.save(user);
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        tokenBlacklist.blacklistToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}