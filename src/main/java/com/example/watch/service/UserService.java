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
        u.setStatus(dto.getStatus());
        return repo.save(u);
    }

    public User update(Long id, UserDTO dto) {
        User u = findById(id);
        u.setName(dto.getName());
        u.setPhone(dto.getPhone());
        u.setRole(dto.getRole());
        u.setGender(dto.getGender());
        u.setUpdatedAt(LocalDateTime.now());
        u.setStatus(dto.getStatus());
        u.setAddress(u.getAddress());
        return repo.save(u);
    }

    public void delete(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        // Xóa giỏ hàng liên quan
        cartRepo.deleteByUserUserId(id);

        // Xóa đánh giá liên quan
        reviewRepo.deleteByUserUserId(id);

        // Xóa chi tiết đơn hàng và đơn hàng liên quan
        List<Order> orders = orderRepo.findByUserId(id);
        for (Order order : orders) {
            orderDetailRepo.deleteByOrderId(order.getOrderId());
            orderRepo.delete(order);
        }

        // Xóa người dùng
        repo.delete(user);
    }

    public void softDelete(Long id) {
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        user.setStatus("banned"); // Giả sử bạn có enum UserStatus
        user.setUpdatedAt(LocalDateTime.now());
        repo.save(user);
    }
}