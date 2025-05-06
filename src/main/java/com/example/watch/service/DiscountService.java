package com.example.watch.service;

import com.example.watch.entity.dto.DiscountDTO;
import java.util.List;

public interface DiscountService {
    DiscountDTO create(DiscountDTO dto);
    DiscountDTO getById(Long id);
    DiscountDTO getByCode(String code);
    List<DiscountDTO> getAll();
    DiscountDTO update(Long id, DiscountDTO dto);
    void delete(Long id);
}
