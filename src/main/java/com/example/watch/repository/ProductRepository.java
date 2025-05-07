package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name, Pageable pageable);
    Optional<Product> findByIdAndDeletedFalse(Long id);
}