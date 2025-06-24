package com.example.watch.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.watch.dto.CreateProductMultipartRequest;
import com.example.watch.dto.ProductDTO;
import com.example.watch.dto.ProductImageDTO;
import com.example.watch.dto.UpdateProductMultipartRequest;
import com.example.watch.entity.*;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.BrandRepository;
import com.example.watch.repository.CategoryRepository;
import com.example.watch.repository.ProductImageRepository;
import com.example.watch.repository.ProductRepository;
import com.example.watch.specification.ProductSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class ProductService {

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    @Autowired
    private Cloudinary cloudinary;

    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final CategoryRepository categoryRepo;
    private final ProductImageRepository imageRepo;
    public ProductService(ProductRepository productRepo,
                          BrandRepository brandRepo,
                          CategoryRepository categoryRepo, ProductImageRepository imageRepo) {
        this.productRepo = productRepo;
        this.brandRepo = brandRepo;
        this.categoryRepo = categoryRepo;
        this.imageRepo = imageRepo;
    }

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public Product findById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    @Transactional
    public ProductDTO createProductWithImages(CreateProductMultipartRequest req) {
        // --- 1. Build + save Product ---
        Product p = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .sku(req.getSku())
                .status(req.getStatus() != null ? req.getStatus() : "available")
                .soldQuantity(req.getSoldQuantity() != null ? req.getSoldQuantity() : 0)
                .remainQuantity(req.getRemainQuantity() != null ? req.getRemainQuantity() : 0)
                .brand(brandRepo.findById(req.getBrandId())
                        .orElseThrow(() -> new IllegalArgumentException("Brand không tồn tại")))
                .category(req.getCategoryId() != null
                        ? categoryRepo.findById(req.getCategoryId()).orElse(null)
                        : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .active(true)
                .build();
        Product saved = productRepo.save(p);

        // --- 2. Tạo folder uploads nếu chưa có ---
        Path uploadPath = Paths.get(uploadDir);
        if (Files.notExists(uploadPath)) {
            try { Files.createDirectories(uploadPath); }
            catch (IOException e) { throw new RuntimeException("Không tạo được thư mục uploads", e); }
        }

        // --- 3. Lưu file + ghi entries ProductImage ---
        List<MultipartFile> files = req.getImages();
        int primaryIndex = req.getPrimaryImageIndex() != null ? req.getPrimaryImageIndex() : 0;

        List<ProductImage> images = IntStream.range(0, files.size())
                .mapToObj(idx -> {
                    MultipartFile file = files.get(idx);
                    try {
                        Map<?,?> uploadResult = cloudinary.uploader()
                                .upload(file.getBytes(), ObjectUtils.asMap("folder", "watch_images"));
                        String secureUrl = uploadResult.get("secure_url").toString();
                        boolean isPrimary = (idx == primaryIndex);

                        return ProductImage.builder()
                                .product(saved)
                                .imageUrl(secureUrl)
                                .isPrimary(isPrimary)
                                .createdAt(LocalDateTime.now())
                                .build();

                    } catch (IOException e) {
                        throw new RuntimeException("Cloudinary upload failed", e);
                    }
                })
                .collect(Collectors.toList());
        imageRepo.saveAll(images);

        // --- 4. Trả về DTO ---
        return ProductDTO.builder()
                .productId(saved.getProductId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .sku(saved.getSku())
                .status(saved.getStatus())
                .soldQuantity(saved.getSoldQuantity())
                .remainQuantity(saved.getRemainQuantity())
                .brandId(saved.getBrand().getBrandId())
                .categoryId(
                        saved.getCategory() != null
                                ? saved.getCategory().getCategoryId()
                                : null)
                .build();
    }

    @Transactional
    public ProductDTO updateProductWithImages(Long id, UpdateProductMultipartRequest req) {
        // 1. Load và cập nhật trường chung
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setPrice(req.getPrice());
        existing.setSku(req.getSku());
        existing.setStatus(req.getStatus() != null ? req.getStatus() : existing.getStatus());
        existing.setSoldQuantity(req.getSoldQuantity() != null ? req.getSoldQuantity() : existing.getSoldQuantity());
        existing.setRemainQuantity(req.getRemainQuantity() != null ? req.getRemainQuantity() : existing.getRemainQuantity());
        existing.setUpdatedAt(LocalDateTime.now());

        if (req.getBrandId() != null) {
            Brand b = brandRepo.findById(req.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id " + req.getBrandId()));
            existing.setBrand(b);
        }
        if (req.getCategoryId() != null) {
            Category c = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + req.getCategoryId()));
            existing.setCategory(c);
        }

        Product saved = productRepo.save(existing);

        // 2. Xóa ảnh cũ không giữ
        List<Long> keepIds = req.getExistingImageIds();
        if (keepIds != null) {
            if (!keepIds.isEmpty()) {
                imageRepo.deleteByProduct_ProductIdAndImageIdNotIn(id, keepIds);
            } else {
                imageRepo.deleteByProduct_ProductId(id);
            }
        }

        // 3. Cập nhật primary cho ảnh cũ theo primaryImageIndex
        if (keepIds != null && !keepIds.isEmpty() && req.getPrimaryImageIndex() != null) {
            int idx = req.getPrimaryImageIndex();
            if (idx >= 0 && idx < keepIds.size()) {
                Long primaryExistingId = keepIds.get(idx);

                // reset tất cả ảnh cũ
                imageRepo.updateAllToNotPrimaryByProductId(id);
                // set lại ảnh chính — chỉ cần 1 arg
                imageRepo.updateIsPrimaryById(primaryExistingId);
            }
        }

        // 4. Upload ảnh mới nếu có
        List<MultipartFile> files = req.getImages();
        if (files != null && !files.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (Files.notExists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    throw new RuntimeException("Could not create upload directory", e);
                }
            }

            int baseExistingCount = keepIds != null ? keepIds.size() : 0;
            Integer newPrimaryIdx = req.getPrimaryImageIndex();
            List<ProductImage> newImages = IntStream.range(0, files.size())
                    .mapToObj(i -> {
                        MultipartFile file = files.get(i);
                        try {
                            Map<?, ?> uploadResult = cloudinary.uploader()
                                    .upload(file.getBytes(), ObjectUtils.asMap("folder", "watch_images"));
                            String url = uploadResult.get("secure_url").toString();
                            boolean isPrimary = (newPrimaryIdx != null
                                    && newPrimaryIdx >= baseExistingCount
                                    && newPrimaryIdx == baseExistingCount + i);
                            return ProductImage.builder()
                                    .product(saved)
                                    .imageUrl(url)
                                    .isPrimary(isPrimary)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                        } catch (IOException ex) {
                            throw new RuntimeException("Cloudinary upload failed", ex);
                        }
                    })
                    .collect(Collectors.toList());

            imageRepo.saveAll(newImages);
        }

        // 5. Nếu gửi primaryImageId, ưu tiên luồng này
        if (req.getPrimaryImageId() != null) {
            imageRepo.updateAllToNotPrimaryByProductId(id);
            ProductImage pi = imageRepo.findById(req.getPrimaryImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with id " + req.getPrimaryImageId()));
            pi.setIsPrimary(true);
            imageRepo.save(pi);
        }

        // 6. Build và trả về DTO
        return ProductDTO.builder()
                .productId(saved.getProductId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .sku(saved.getSku())
                .status(saved.getStatus())
                .soldQuantity(saved.getSoldQuantity())
                .remainQuantity(saved.getRemainQuantity())
                .brandId(saved.getBrand().getBrandId())
                .categoryId(saved.getCategory() != null ? saved.getCategory().getCategoryId() : null)
                .build();
    }

    public void delete(Long id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id " + id);
        }
        productRepo.deleteById(id);
    }

    public List<Product> getByCategory(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID must be provided");
        }
        return productRepo.findProductByCategory(id);
    }

    public List<Product> getAllByCategories() {
        return productRepo.getAllByCategoryies();
    }

    public List<Product> getByBrand(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Category ID must be provided");
        }
        return productRepo.findProductByBrand(id);
    }

    public List<Product> getAllByBrands() {
        return productRepo.getAllByBrands();
    }

    public List<Product> findAllByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepo.findAllByPrice(minPrice, maxPrice);
    }

    // Lọc theo category + price
    public List<Product> findByCategoryAndPrice(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        // kiểm tra category tồn tại nếu cần:
        categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found " + categoryId));
        return productRepo.findByCategoryAndPrice(categoryId, minPrice, maxPrice);
    }

    // Lọc chung theo price trên tất cả categories
    public List<Product> findAllByPriceInCategories(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepo.findAllByPrice(minPrice, maxPrice);
    }

    // Lọc theo brand + price
    public List<Product> findByBrandAndPrice(Long brandId, BigDecimal minPrice, BigDecimal maxPrice) {
        brandRepo.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found " + brandId));
        return productRepo.findByBrandAndPrice(brandId, minPrice, maxPrice);
    }

    // Lọc chung theo price trên tất cả brands
    public List<Product> findAllByPriceInBrands(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepo.findAllByPriceForBrands(minPrice, maxPrice);
    }

    public List<Product> filterProducts(ProductFilter filter) {
        return productRepo.findAll(ProductSpecification.byFilter(filter));
    }

    public void softDelete(Long id) {
        Product product = findById(id);
        product.setStatus("DELETED"); // hoặc: product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        productRepo.save(product);
    }
}