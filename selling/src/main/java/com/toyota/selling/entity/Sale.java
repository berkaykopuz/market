package com.toyota.selling.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="sales")
public class Sale {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String billId;
    private Double totalAmount;
    private Double paidAmount;
    private LocalDateTime saleDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @ManyToMany(mappedBy = "products_sales")
    private Set<Product> products;
}
