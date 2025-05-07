package com.example.watch.service.impl;

import com.example.watch.entity.Category;
import com.example.watch.entity.Product;
import com.example.watch.entity.dto.ProductDTO;
import com.example.watch.repository.CategoryRepository;
import com.example.watch.repository.ProductRepository;
import com.example.watch.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    private ProductDTO toDto(Product e) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(e, dto, "category");
        dto.setCategoryId(e.getCategory() != null ? e.getCategory().getId() : null);
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        Product e = new Product();
        BeanUtils.copyProperties(dto, e, "id", "createdAt", "updatedAt", "deleted", "category");
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            e.setCategory(cat);
        }
        return e;
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Product saved = productRepo.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public ProductDTO getById(Long id) {
        Product e = productRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Product not found or deleted"));
        return toDto(e);
    }

    @Override
    public Page<ProductDTO> getAll(String search, Pageable pageable) {
        Page<Product> page;
        if (search != null && !search.isEmpty()) {
            page = productRepo.findByNameContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else {
            page = productRepo.findByDeletedFalse(pageable);
        }
        return page.map(this::toDto);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        BeanUtils.copyProperties(dto, existing, "id", "createdAt", "deleted", "category");
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(cat);
        }
        Product updated = productRepo.save(existing);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setDeleted(true);
        productRepo.save(existing);
    }
}