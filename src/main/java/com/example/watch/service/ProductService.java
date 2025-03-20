package com.example.watch.service;

import com.example.watch.dto.ProductRequest;
import com.example.watch.entity.Product;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lấy tất cả sản phẩm chưa xóa
    public List<Product> getAllProducts() {
        return productRepository.findAllActiveProducts();
    }

    // Lấy sản phẩm theo ID (chưa xóa)
    public Product getProductById(Long productId) {
        return productRepository.findActiveById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    // Thêm sản phẩm
    @Transactional
    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setCategoryId(request.getCategoryId());
        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    @Transactional
    public Product updateProduct(Long productId, ProductRequest request) {
        Product product = getProductById(productId);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setSku(request.getSku());
        product.setCategoryId(request.getCategoryId());
        return productRepository.save(product);
    }

    // Xóa mềm
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        productRepository.softDelete(productId);
    }
}