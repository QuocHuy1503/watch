package com.example.watch.service;

import com.example.watch.dto.CategoryRequest;
import com.example.watch.dto.CategoryResponse;
import com.example.watch.entity.Category;
import com.example.watch.exception.ConflictException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapToResponse(category);
    }

    // Thêm danh mục
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        validateParentCategory(request.getParentCategoryId());

        Category category = new Category();
        category.setName(request.getName());
        setParentCategory(category, request.getParentCategoryId());

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    // Cập nhật danh mục
    @Transactional
    public CategoryResponse updateCategory(Integer categoryId, CategoryRequest request) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Kiểm tra không được chọn chính mình làm parent
        if (request.getParentCategoryId() != null && request.getParentCategoryId().equals(categoryId)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }

        validateParentCategory(request.getParentCategoryId());

        category.setName(request.getName());
        setParentCategory(category, request.getParentCategoryId());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    // Xóa mềm
    @Transactional
    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepository.hasChildren(categoryId)) {
            throw new ConflictException("Cannot delete category with subcategories");
        }

        categoryRepository.softDelete(categoryId);
    }

    // Helper: Validate parent category
    private void validateParentCategory(Integer parentCategoryId) {
        if (parentCategoryId != null && parentCategoryId != 0) {
            if (!categoryRepository.existsById(parentCategoryId)) {
                throw new ResourceNotFoundException("Parent category not found");
            }
            if (categoryRepository.findById(parentCategoryId).get().isDeleted()) {
                throw new ConflictException("Parent category is deleted");
            }
        }
    }

    // Helper: Set parent category
    private void setParentCategory(Category category, Integer parentCategoryId) {
        if (parentCategoryId == null || parentCategoryId == 0) {
            category.setParentCategory(null);
        } else {
            Category parent = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParentCategory(parent);
        }
    }

    // Convert Entity → DTO
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setName(category.getName());
        response.setParentCategoryId(
                category.getParentCategory() != null
                        ? category.getParentCategory().getCategoryId()
                        : null
        );
        response.setHasChildren(categoryRepository.hasChildren(category.getCategoryId()));
        return response;
    }
}