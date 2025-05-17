package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        SELECT p.* 
        FROM products p
        LEFT JOIN categories c 
          ON p.category_id = c.category_id
        WHERE p.category_id = :categoryId
        """,
            nativeQuery = true)
    List<Product> findProductByCategory(@Param("categoryId") Long categoryId);

    @Query(value = """
        SELECT p.* 
        FROM products p
        LEFT JOIN categories c 
          ON p.category_id = c.category_id
        """,
            nativeQuery = true)
    List<Product> getAllByCategoryies();

    @Query(value = """
        SELECT p.* 
        FROM products p
        LEFT JOIN brands b 
          ON p.brand_id = b.brand_id
        WHERE p.brand_id = :brandId
        """,
            nativeQuery = true)
    List<Product> findProductByBrand(@Param("brandId") Long brandId);

    @Query(value = """
        SELECT p.* 
        FROM products p
        LEFT JOIN brands c 
          ON p.brand_id = c.brand_id
        """,
            nativeQuery = true)
    List<Product> getAllByBrands();
    // additional query methods if needed
}