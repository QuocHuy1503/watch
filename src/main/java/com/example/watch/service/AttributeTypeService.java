package com.example.watch.service;

import com.example.watch.entity.AttributeType;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.AttributeTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AttributeTypeService {
    private final AttributeTypeRepository repo;

    public AttributeTypeService(AttributeTypeRepository repo) {
        this.repo = repo;
    }

    public List<AttributeType> findAll() {
        return repo.findAll();
    }

    public AttributeType findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeType not found with id " + id));
    }

    public AttributeType create(AttributeType type) {
        return repo.save(type);
    }

    public AttributeType update(Long id, AttributeType dto) {
        AttributeType existing = findById(id);
        existing.setName(dto.getName());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("AttributeType not found with id " + id);
        }
        repo.deleteById(id);
    }
}