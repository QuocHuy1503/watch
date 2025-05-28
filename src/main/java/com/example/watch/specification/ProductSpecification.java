package com.example.watch.specification;

import com.example.watch.entity.Product;
import com.example.watch.entity.AttributeValue;
import com.example.watch.entity.ProductFilter;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> byFilter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Price
            if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
                predicates.add(cb.between(root.get("price"), filter.getMinPrice(), filter.getMaxPrice()));
            }

            // Brands
            if (filter.getBrandIds() != null && !filter.getBrandIds().isEmpty()) {
                predicates.add(root.get("brand").get("brandId").in(filter.getBrandIds()));
            }

            // Categories
            if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
                predicates.add(root.get("category").get("categoryId").in(filter.getCategoryIds()));
            }
            int i = 0;
            // Attributes
            if (filter.getAttributes() != null && !filter.getAttributes().isEmpty()) {
                for (ProductFilter.AttributeFilter af : filter.getAttributes()) {
                    // join attribute_values
                    Join<Product, AttributeValue> join = root.join("attributeValues", JoinType.INNER);
                    join.alias("av" + (i++));

                    predicates.add(cb.and(
                            cb.equal(join.get("attributeType").get("attrTypeId"), af.getAttrTypeId()),
                            join.get("value").in(af.getValues())
                    ));
                }
                // distinct
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}