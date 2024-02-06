package com.toyota.selling.dto;

public class SaleRequest {
    private Long productId;
    private Integer requestedQuantity;
    private Long campaignId;

    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Long getCampaignId() {
        return campaignId;
    }
}
