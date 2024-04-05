package com.toyota.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties("sale")
    @OneToMany(mappedBy = "sale",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProductSale> productSales;
    private boolean deleted = Boolean.FALSE;

    public Sale() {
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
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

    public Set<ProductSale> getProductSales() {
        return productSales;
    }

    public void setProductSales(Set<ProductSale> productSales) {
        this.productSales = productSales;
    }
}
