package com.toyota.selling.dto;

import com.toyota.selling.entity.Campaign;
import com.toyota.selling.entity.Product;

import java.time.LocalDateTime;
import java.util.Set;

public record CampaignDto (
        Long id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer requiredQuantity,
        Integer discountQuantity,
        Double discountAmount
){
    public static CampaignDto convert(Campaign from){
        return new CampaignDto(from.getId(),
                from.getName(),
                from.getStartDate(),
                from.getEndDate(),
                from.getRequiredQuantity(),
                from.getDiscountQuantity(),
                from.getDiscountAmount()
        );
    }
}
