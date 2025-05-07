package com.example.watch.service;

import com.example.watch.entity.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO create(ProductDTO dto);
    ProductDTO getById(Long id);
    Page<ProductDTO> getAll(String search, Pageable pageable);
    ProductDTO update(Long id, ProductDTO dto);
    void delete(Long id);
}