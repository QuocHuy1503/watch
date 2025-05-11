package com.example.watch.service;

import com.example.watch.entity.Brand;
import com.example.watch.entity.Category;
import com.example.watch.entity.Product;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.BrandRepository;
import com.example.watch.repository.CategoryRepository;
import com.example.watch.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final CategoryRepository categoryRepo;

    public ProductService(ProductRepository productRepo,
                          BrandRepository brandRepo,
                          CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.brandRepo = brandRepo;
        this.categoryRepo = categoryRepo;
    }

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public Product findById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    public Product create(Product product) {
        return productRepo.save(product);
    }

    public Product update(Long id, Product dto) {
        Product existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setSku(dto.getSku());
        existing.setStatus(dto.getStatus());
        existing.setSoldQuantity(dto.getSoldQuantity());
        existing.setRemainQuantity(dto.getRemainQuantity());

        if (dto.getBrand() != null) {
            Brand b = brandRepo.findById(dto.getBrand().getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id " + dto.getBrand().getBrandId()));
            existing.setBrand(b);
        }
        if (dto.getCategory() != null) {
            Category c = categoryRepo.findById(dto.getCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategory().getCategoryId()));
            existing.setCategory(c);
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return productRepo.save(existing);
    }

    public void delete(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        }
        productRepo.deleteById(id);
    }
}