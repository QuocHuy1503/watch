package com.example.watch.service;

import com.example.watch.entity.*;
import com.example.watch.dto.ReviewDTO;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository repo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public ReviewService(ReviewRepository repo, UserRepository userRepo, ProductRepository productRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public List<ReviewDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ReviewDTO findById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));
    }

    public ReviewDTO create(ReviewDTO dto) {
        Review r = new Review();
        r.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        r.setProduct(productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        r = repo.save(r);
        return toDto(r);
    }

    public ReviewDTO update(Long id, ReviewDTO dto) {
        Review r = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        r = repo.save(r);
        return toDto(r);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private ReviewDTO toDto(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(r.getReviewId());
        dto.setUserId(r.getUser().getUserId());
        dto.setProductId(r.getProduct().getProductId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    public List<ReviewDTO> findByProductId(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        return repo.findByProduct(product).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}