package com.example.watch.service;

import com.example.watch.entity.ProductImage;
import com.example.watch.entity.Product;
import com.example.watch.dto.ProductImageDTO;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.ProductImageRepository;
import com.example.watch.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Service
@Transactional
public class ProductImageService {
    private final ProductImageRepository repo;
    private final ProductRepository productRepo;
    private final ProductService productService;

    public ProductImageService(ProductImageRepository repo, ProductRepository productRepo, ProductService productService) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.productService = productService;
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

    public ProductImage uploadImage(Long productId, MultipartFile file, Boolean isPrimary) {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            String uploadDir = "uploads";
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = Paths.get(uploadDir, filename);

            // ✅ Viết luồng phụ để xử lý lưu
            new Thread(() -> {
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save file", e);
                } finally {
                    latch.countDown(); // báo hiệu xong
                }
            }).start();

            // ✅ Chờ lưu file xong
            latch.await();

            // ✅ Tiếp tục lưu DB
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setImageUrl("/uploads/" + filename);
            img.setIsPrimary(isPrimary != null ? isPrimary : false);
            return repo.save(img);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}