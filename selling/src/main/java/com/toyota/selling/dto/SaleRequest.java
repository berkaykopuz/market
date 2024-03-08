package com.toyota.selling.dto;

import com.toyota.selling.entity.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class SaleRequest {
    private Long productId;
    private Integer requestedAmount;
    private Long campaignId;

    public SaleRequest(Long productId, Integer requestedAmount, Long campaignId) {
        this.productId = productId;
        this.requestedAmount = requestedAmount;
        this.campaignId = campaignId;
    }

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
