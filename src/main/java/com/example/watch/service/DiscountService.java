package com.example.watch.service;

import com.example.watch.entity.Discount;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DiscountService {
    private final DiscountRepository repo;

    public DiscountService(DiscountRepository repo) {
        this.repo = repo;
    }

    public List<Discount> findAll() {
        return repo.findAll();
    }

    public Discount findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id " + id));
    }

    public Discount create(Discount discount) {
        return repo.save(discount);
    }

    public Discount update(Long id, Discount dto) {
        Discount existing = findById(id);
        existing.setCode(dto.getCode());
        existing.setType(dto.getType());
        existing.setValue(dto.getValue());
        existing.setValidFrom(dto.getValidFrom());
        existing.setValidUntil(dto.getValidUntil());
        existing.setMaxUses(dto.getMaxUses());
        existing.setActive(dto.getActive());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Discount not found with id " + id);
        }
        repo.deleteById(id);
    }
}
