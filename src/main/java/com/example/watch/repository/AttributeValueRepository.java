package com.example.watch.repository;

import com.example.watch.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    List<AttributeValue> findByProductProductId(Long productId);
}