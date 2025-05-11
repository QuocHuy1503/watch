package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // additional query methods if needed
}