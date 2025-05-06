package com.example.watch.service.impl;

import com.example.watch.entity.dto.DiscountDTO;
import com.example.watch.entity.Discount;
import com.example.watch.repository.DiscountRepository;
import com.example.watch.service.DiscountService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository repository;

    public DiscountServiceImpl(DiscountRepository repository) {
        this.repository = repository;
    }

    private DiscountDTO toDto(Discount entity) {
        DiscountDTO dto = new DiscountDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private Discount toEntity(DiscountDTO dto) {
        Discount entity = new Discount();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    public DiscountDTO create(DiscountDTO dto) {
        Discount saved = repository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public DiscountDTO getById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    @Override
    public DiscountDTO getByCode(String code) {
        return repository.findByCode(code)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
    }

    @Override
    public List<DiscountDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DiscountDTO update(Long id, DiscountDTO dto) {
        Discount existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        BeanUtils.copyProperties(dto, existing, "id", "createdAt");
        Discount updated = repository.save(existing);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}