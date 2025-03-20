package com.example.watch.service;

import com.example.watch.dto.ProductRequest;
import com.example.watch.dto.ProductResponse;
import com.example.watch.entity.*;
import com.example.watch.exception.*;
import com.example.watch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Lấy tất cả sản phẩm
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllActiveProducts().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Lấy sản phẩm theo ID
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapToResponse(product);
    }

    // Thêm sản phẩm
    @Transactional
    public ProductResponse createProduct(ProductRequest request) throws DuplicateResourceException {
        // Kiểm tra SKU trùng
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists: " + request.getSku());
        }

        // Kiểm tra category tồn tại và chưa bị xóa
        Category category = categoryRepository.findActiveById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setCategory(category); // Liên kết với category

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    // Cập nhật sản phẩm
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) throws DuplicateResourceException {
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Kiểm tra SKU mới có trùng với sản phẩm khác không
        if (!product.getSku().equals(request.getSku()) &&
                productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists: " + request.getSku());
        }

        // Kiểm tra category
        Category category = categoryRepository.findActiveById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    // Xóa mềm
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.softDelete(productId);
    }

    // Convert Entity → Response DTO
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setBrand(product.getBrand());
        response.setSku(product.getSku());
        response.setCategoryId(product.getCategory().getCategoryId());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}