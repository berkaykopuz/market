package com.toyota.product.dto;

import com.toyota.product.entity.Product;

import java.time.LocalDateTime;
import java.util.Date;

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
