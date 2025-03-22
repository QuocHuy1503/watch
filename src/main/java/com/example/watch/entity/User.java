package com.example.watch.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE user_id = ?") // Soft delete
@Where(clause = "deleted = false") // Mặc định filter các user chưa xóa
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password hash is required")
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 100)
    private String fullName;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @Column(length = 20)
    private String phone;

    @NotBlank(message = "Role is required")
    @Column(nullable = false, length = 20)
    private String role; // Ví dụ: customer, staff, admin

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted;
}