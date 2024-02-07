package com.toyota.selling.dto;

import com.toyota.selling.entity.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SaleRequest {
    private Long productId;
    private Integer requestedAmount;
    private Long campaignId;


    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedAmount() {
        return requestedAmount;
    }

    public Long getCampaignId() {
        return campaignId;
    }
}
