package com.example.watch.service;

import com.example.watch.entity.Brand;
import com.example.watch.entity.Category;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository repo;

    public CategoryService(CategoryRepository repo) {
        this.repo = repo;
    }

    public List<Category> findAll() {
        return repo.findAll();
    }

    public Category findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    public Category create(Category category) {
        return repo.save(category);
    }

    public Category update(Long id, Category dto) {
        Category existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setStatus(dto.getStatus());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id " + id);
        }
        repo.deleteById(id);
    }

    public void softDelete(Long id) {
        Category existing = findById(id);
        existing.setStatus("locked"); // Giả sử bạn có enum UserStatus
        existing.setUpdatedAt(LocalDateTime.now());
        repo.save(existing);
    }
}