package com.toyota.selling.dto;

import com.toyota.selling.entity.*;

import java.time.LocalDateTime;
import java.util.Set;

public record SaleDto(
        String billId,
        Double totalPrice,
        Double paidPrice,
        LocalDateTime saleDate,
        PaymentMethod paymentMethod,
        Set<ProductSale> productSales
){
    public static SaleDto convert(Sale from){
        return new SaleDto(
                from.getBillId(),
                from.getTotalPrice(),
                from.getPaidPrice(),
                from.getSaleDate(),
                from.getPaymentMethod(),
                from.getProductSales()
        );
    }
}
