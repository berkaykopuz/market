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
    private Double totalPrice;
    private Double paidPrice;
    private LocalDateTime saleDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @ManyToMany(mappedBy = "products_sales")
    private Set<Product> products;
    public Sale() {
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(Double paidPrice) {
        this.paidPrice = paidPrice;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
