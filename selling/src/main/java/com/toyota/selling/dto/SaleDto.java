package com.toyota.selling.dto;

import com.toyota.selling.entity.Campaign;
import com.toyota.selling.entity.PaymentMethod;
import com.toyota.selling.entity.Product;
import com.toyota.selling.entity.Sale;

import java.time.LocalDateTime;
import java.util.Set;

public record SaleDto(
        String billId,
        Double totalPrice,
        Double paidPrice,
        LocalDateTime saleDate,
        PaymentMethod paymentMethod,
        Set<Product> products
){
    public static SaleDto convert(Sale from){
        return new SaleDto(
                from.getBillId(),
                from.getTotalPrice(),
                from.getPaidPrice(),
                from.getSaleDate(),
                from.getPaymentMethod(),
                from.getProducts()
        );
    }
}
