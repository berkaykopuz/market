package com.toyota.selling.dto;

import com.toyota.selling.entity.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

public class SaleRequestBody {
    private List<SaleRequest> saleRequests;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public List<SaleRequest> getSaleRequests() {
        return saleRequests;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
