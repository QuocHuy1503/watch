package com.example.watch.repository;

import com.example.watch.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserUserId(Long userId);

    void deleteByUserUserId(Long userId);

    @Query(value = "SELECT * FROM carts WHERE user_id = :userId", nativeQuery = true)
    List<Cart> findAllCartsByUserId(@Param("userId") Long userId);
}