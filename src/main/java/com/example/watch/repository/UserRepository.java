package com.example.watch.repository;

import com.example.watch.entity.User;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    // Lấy user chưa xóa theo ID
    @Query("SELECT u FROM User u WHERE u.userId = ?1 AND u.deleted = false")
    Optional<User> findActiveById(Long userId);

    // Lấy tất cả user chưa xóa
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActiveUsers();

    // Kiểm tra email tồn tại
    boolean existsByEmail(String email);

    // Xóa mềm
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.userId = ?1")
    void softDelete(Long userId);
}