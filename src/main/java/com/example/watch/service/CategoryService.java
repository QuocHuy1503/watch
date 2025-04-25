package com.example.watch.service;

import com.example.watch.entity.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    CategoryDTO create(CategoryDTO dto);
    CategoryDTO getById(Long id);
    List<CategoryDTO> getAll();
    CategoryDTO update(Long id, CategoryDTO dto);
    void delete(Long id);
}
