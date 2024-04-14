package com.toyota.report.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class ProductSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "billId")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private int saledAmount;

    private double saledPrice;
    private String usedCampaign;

    public double getSaledPrice() {
        return saledPrice;
    }

    public void setSaledPrice(double saledPrice) {
        this.saledPrice = saledPrice;
    }

    public String getUsedCampaign() {
        return usedCampaign;
    }

    public void setUsedCampaign(String usedCampaign) {
        this.usedCampaign = usedCampaign;
    }

    public ProductSale() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getSaledAmount() {
        return saledAmount;
    }

    public void setSaledAmount(int saledAmount) {
        this.saledAmount = saledAmount;
    }
}
