package com.example.watch.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
}