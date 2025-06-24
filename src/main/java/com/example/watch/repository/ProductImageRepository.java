package com.example.watch.repository;

import com.example.watch.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    @Query("SELECT pi FROM ProductImage pi JOIN FETCH pi.product WHERE pi.product.productId = :productId")
    List<ProductImage> findByProductProductId(@Param("productId") Long productId);
    void deleteByProduct_ProductId(Long productId);

    void deleteByProduct_ProductIdAndImageIdNotIn(Long productId, List<Long> existingImageIds);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.productId = :pid")
    void updateAllToNotPrimaryByProductId(@Param("pid") Long productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = true WHERE pi.imageId = :iid")
    void updateIsPrimaryById(@Param("iid") Long imageId);
}