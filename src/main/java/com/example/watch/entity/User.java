package com.example.watch.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Entity
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private String role = "customer";

    @Pattern(regexp = "[MF]")
    private String gender;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    // Hash plain password using MD5
    public void setPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(plain.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            this.passwordHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    // getters and setters
    // ... các import và annotation khác
    private String resetToken;

    // getter & setter cho resetToken
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    // getters and setters (omit password setter to enforce hashing)
}