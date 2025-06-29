package com.example.watch.repository;

import com.example.watch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String token);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}