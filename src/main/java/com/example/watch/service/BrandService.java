package com.example.watch.service;

import com.example.watch.entity.Brand;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BrandService {
    private final BrandRepository repo;

    public BrandService(BrandRepository repo) {
        this.repo = repo;
    }

    public List<Brand> findAll() {
        return repo.findAll();
    }

    public Brand findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id " + id));
    }

    public Brand create(Brand brand) {
        return repo.save(brand);
    }

    public Brand update(Long id, Brand dto) {
        Brand existing = findById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found with id " + id);
        }
        repo.deleteById(id);
    }
}