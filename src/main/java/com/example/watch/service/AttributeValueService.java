package com.example.watch.service;

import com.example.watch.entity.AttributeValue;
import com.example.watch.entity.AttributeType;
import com.example.watch.entity.Product;
import com.example.watch.exception.ResourceNotFoundException;
import com.example.watch.repository.AttributeValueRepository;
import com.example.watch.repository.AttributeTypeRepository;
import com.example.watch.repository.ProductRepository;
import com.example.watch.dto.AttributeValueDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AttributeValueService {
    private final AttributeValueRepository repo;
    private final ProductRepository productRepo;
    private final AttributeTypeRepository typeRepo;

    public AttributeValueService(AttributeValueRepository repo,
                                 ProductRepository productRepo,
                                 AttributeTypeRepository typeRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.typeRepo = typeRepo;
    }

    public List<AttributeValue> findByProduct(Long productId) {
        return repo.findByProductProductId(productId);
    }

    public AttributeValue findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue not found with id " + id));
    }

    public AttributeValue create(AttributeValueDTO dto) {
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + dto.getProductId()));
        AttributeType type = typeRepo.findById(dto.getAttrTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("AttributeType not found with id " + dto.getAttrTypeId()));
        AttributeValue av = new AttributeValue();
        av.setProduct(product);
        av.setAttributeType(type);
        av.setValue(dto.getValue());
        return repo.save(av);
    }

    public AttributeValue update(Long id, AttributeValueDTO dto) {
        AttributeValue av = findById(id);
        av.setValue(dto.getValue());
        // Optionally update associations
        return repo.save(av);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("AttributeValue not found with id " + id);
        }
        repo.deleteById(id);
    }
}