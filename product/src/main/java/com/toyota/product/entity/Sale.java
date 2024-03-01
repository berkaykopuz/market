package com.toyota.product.entity;

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
    private Double totalPrice;
    private Double paidPrice;
    private LocalDateTime saleDate;
    private String cashierName;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @OneToMany(mappedBy = "sale",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<ProductSale> productSales;
}
