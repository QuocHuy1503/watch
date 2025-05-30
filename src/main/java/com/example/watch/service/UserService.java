package com.example.watch.service;

import com.example.watch.dto.*;
import com.example.watch.entity.*;
import com.example.watch.repository.*;
import com.example.watch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository repo;

    private final CartRepository cartRepo;

    private final OrderRepository orderRepo;

    private final ReviewRepository reviewRepo;

    private final OrderDetailRepository orderDetailRepo;

    public UserService(UserRepository repo
            , CartRepository cartRepository
            , OrderRepository orderRepository
            , ReviewRepository reviewRepository
            , OrderDetailRepository orderDetailRepository) {
        this.repo = repo;
        this.cartRepo = cartRepository;
        this.orderRepo = orderRepository;
        this.reviewRepo = reviewRepository;
        this.orderDetailRepo = orderDetailRepository;
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public User create(UserDTO dto) {
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setRole(dto.getRole());
        u.setGender(dto.getGender());
        u.setPassword(dto.getPassword()); // hashed internally
        return repo.save(u);
    }

    public User update(Long id, UserDTO dto) {
        User u = findById(id);
        u.setName(dto.getName());
        u.setPhone(dto.getPhone());
        u.setRole(dto.getRole());
        u.setGender(dto.getGender());
        u.setUpdatedAt(LocalDateTime.now());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            u.setPassword(dto.getPassword());
        }
        return repo.save(u);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        Order order = orderRepo.findByUserId(id);
        orderDetailRepo.deleteByOrderId(order.getOrderId());
        orderRepo.delete(order);
        cartRepo.deleteByUserUserId(id);
        orderRepo.deleteByUserId(id);
        reviewRepo.deleteById(id);
        repo.deleteById(id);
    }
}