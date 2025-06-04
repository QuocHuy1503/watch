package com.example.watch.repository;

import com.example.watch.entity.Product;
import com.example.watch.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserUserId(Long userId);

    List<Review> findByProduct(Product product);
    @Modifying
    @Query(value = "DELETE FROM Review r WHERE r.user.id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);
}