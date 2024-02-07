package com.toyota.selling.dto;

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
