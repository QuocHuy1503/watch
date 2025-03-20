package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm sản phẩm chưa xóa theo ID
    @Query("SELECT p FROM Product p WHERE p.productId = ?1 AND p.deleted = false")
    Optional<Product> findActiveById(Long productId);

    // Lấy tất cả sản phẩm chưa xóa
    @Query("SELECT p FROM Product p WHERE p.deleted = false")
    List<Product> findAllActiveProducts();

    // Xóa mềm
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true WHERE p.productId = ?1")
    void softDelete(Long productId);
}