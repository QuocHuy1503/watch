package com.example.watch.service;

import com.example.watch.dto.*;
import com.example.watch.entity.*;
import com.example.watch.exception.*;
import com.example.watch.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Lấy tất cả user
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllActiveUsers().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Lấy user theo ID
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    // Tạo user
    @Transactional
    public UserResponse createUser(UserRequest request) throws DuplicateResourceException {
        // Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPasswordHash());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    // Cập nhật user
    @Transactional
    public UserResponse updateUser(Long userId, UserRequest request) throws DuplicateResourceException {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Kiểm tra email mới có trùng với user khác không
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }

        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPasswordHash());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    // Xóa mềm
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.softDelete(userId);
    }

    // Convert Entity → Response DTO
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}