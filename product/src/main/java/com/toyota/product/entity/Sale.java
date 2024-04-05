package com.toyota.product.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name="sales")
@SQLDelete(sql="UPDATE sales SET deleted = true WHERE billId=?")
@Where(clause = "deleted=false")
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
    private boolean deleted = Boolean.FALSE;
}
