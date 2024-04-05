package com.toyota.selling.dto;

import com.toyota.selling.entity.Product;

import java.time.LocalDateTime;

public record ProductDto(Long id,
                         String name,
                         Integer amount,
                         Double price,
                         String category,
                         LocalDateTime updatedDate)
{

    public static ProductDto convert(Product from){
        return new ProductDto(from.getId(),
                from.getName(),
                from.getAmount(),
                from.getPrice(),
                from.getCategory(),
                from.getUpdatedDate());
    }

}
