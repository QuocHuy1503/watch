package com.example.watch.repository;

import com.example.watch.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT pi FROM ProductImage pi JOIN FETCH pi.product WHERE pi.product.productId = :productId")
    List<ProductImage> findByProductProductId(@Param("productId") Long productId);
}