package com.example.watch.repository;

import com.example.watch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    // 1. Category + Price
    @Query(value = """
        SELECT p.* 
          FROM products p
         WHERE p.category_id = :categoryId
           AND p.price BETWEEN :minPrice AND :maxPrice
        """,
            nativeQuery = true)
    List<Product> findByCategoryAndPrice(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);

    // 2. All Categories + Price
    @Query(value = """
        SELECT p.* 
          FROM products p
         WHERE p.price BETWEEN :minPrice AND :maxPrice
        """,
            nativeQuery = true)
    List<Product> findAllByPrice(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);


    // 3. Brand + Price
    @Query(value = """
        SELECT p.* 
          FROM products p
         WHERE p.brand_id = :brandId
           AND p.price BETWEEN :minPrice AND :maxPrice
        """,
            nativeQuery = true)
    List<Product> findByBrandAndPrice(
            @Param("brandId")   Long brandId,
            @Param("minPrice")  BigDecimal minPrice,
            @Param("maxPrice")  BigDecimal maxPrice);

    // 4. All Brands + Price
    @Query(value = """
        SELECT p.* 
          FROM products p
         WHERE p.price BETWEEN :minPrice AND :maxPrice
        """,
            nativeQuery = true)
    List<Product> findAllByPriceForBrands(  // tên chỉ để phân biệt
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice);
}