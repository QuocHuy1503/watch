package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Lấy sản phẩm chưa xóa theo ID
    @Query("SELECT p FROM Product p WHERE p.productId = ?1 AND p.deleted = false")
    Optional<Product> findActiveById(Long productId);

    // Lấy tất cả sản phẩm chưa xóa
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActiveProducts();

    // Kiểm tra SKU tồn tại
    boolean existsBySku(String sku);

    // Xóa mềm
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.productId = ?1")
    void softDelete(Long productId);
}