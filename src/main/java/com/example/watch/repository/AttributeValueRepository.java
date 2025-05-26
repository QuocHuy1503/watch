package com.example.watch.repository;

import com.example.watch.dto.AttributeFilterDTO;
import com.example.watch.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    List<AttributeValue> findByProductProductId(Long productId);

    @Query(value = """
        SELECT
          at.attr_type_id    AS attrTypeId,
          at.name            AS typeName,
          array_agg(DISTINCT av.value ORDER BY av.value) AS values
        FROM attribute_types at
          JOIN attribute_values av
            ON at.attr_type_id = av.attr_type_id
        GROUP BY at.attr_type_id, at.name
        ORDER BY at.name
        """, nativeQuery = true)
    List<Object[]> findRawFilters();

    default List<AttributeFilterDTO> findAllFilters() {
        return findRawFilters().stream()
                .map(row -> new AttributeFilterDTO(
                        ((Number)row[0]).longValue(),
                        (String)row[1],
                        // Postgres JDBC sẽ cast array thành java Object[], cần chuyển về List<String>
                        java.util.Arrays.asList((String[])row[2])
                ))
                .toList();
    }
}