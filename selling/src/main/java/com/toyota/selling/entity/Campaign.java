package com.toyota.selling.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @ManyToMany(mappedBy = "products_campaigns")
    private Set<Product> discountedProducts;
    private Integer requiredQuantity;
    private Integer discountQuantity;
    private Double discountAmount;
}
