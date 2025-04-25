package com.example.watch.service.impl;

import com.example.watch.entity.dto.CategoryDTO;
import com.example.watch.entity.Category;
import com.example.watch.repository.CategoryRepository;
import com.example.watch.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    private CategoryDTO toDto(Category e) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

    private Category toEntity(CategoryDTO dto) {
        Category e = new Category();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        Category saved = repository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public CategoryDTO getById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<CategoryDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        BeanUtils.copyProperties(dto, existing, "id", "createdAt");
        Category updated = repository.save(existing);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setDeleted(true);
        repository.save(existing);
    }
}