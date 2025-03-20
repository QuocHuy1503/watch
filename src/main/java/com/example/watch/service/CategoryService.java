package com.example.watch.service;

import com.example.watch.dto.CategoryRequest;
import com.example.watch.dto.CategoryResponse;
import com.example.watch.entity.Category;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Lấy tất cả danh mục
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllActiveCategories().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Lấy danh mục theo ID
    public CategoryResponse getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return mapToResponse(category);
    }

    // Thêm danh mục
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findActiveById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    // Cập nhật danh mục
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, CategoryRequest request) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findActiveById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    // Xóa mềm
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        categoryRepository.softDelete(categoryId);
    }

    // Convert Entity → Response DTO
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setName(category.getName());
        response.setParentCategoryId(
                category.getParentCategory() != null
                        ? category.getParentCategory().getCategoryId()
                        : null
        );
        return response;
    }
}