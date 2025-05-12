package com.example.watch.service;

import com.example.watch.entity.ProductImage;
import com.example.watch.entity.Product;
import com.example.watch.dto.ProductImageDTO;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.ProductImageRepository;
import com.example.watch.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductImageService {
    private final ProductImageRepository repo;
    private final ProductRepository productRepo;

    public ProductImageService(ProductImageRepository repo, ProductRepository productRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
    }

    public List<ProductImage> findAllByProduct(Long productId) {
        return repo.findByProductProductId(productId);
    }

    public ProductImage findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id " + id));
    }

    public ProductImage create(ProductImageDTO dto) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + dto.getProductId()));
        ProductImage img = new ProductImage();
        img.setProduct(product);
        img.setImageUrl(dto.getImageUrl());
        img.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        return repo.save(img);
    }

    public ProductImage update(Long id, ProductImageDTO dto) {
        ProductImage img = findById(id);
        img.setImageUrl(dto.getImageUrl());
        img.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : img.getIsPrimary());
        img.setProduct(productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + dto.getProductId())));
        return repo.save(img);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Image not found with id " + id);
        }
        repo.deleteById(id);
    }
}