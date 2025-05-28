package com.example.watch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank @Size(max = 150)
    private String name;

    private String description;

    @NotNull @DecimalMin("0.00")
    private BigDecimal price;

    @Size(max = 50)
    private String sku;

    @NotBlank
    private String status = "available";

    private Integer soldQuantity = 0;

    private Integer remainQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AttributeValue> attributeValues;

    // getters and setters
}