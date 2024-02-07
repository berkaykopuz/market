package com.toyota.selling.controller;

import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.dto.SaleRequestBody;
import com.toyota.selling.entity.PaymentMethod;
import com.toyota.selling.service.SellingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/selling")
public class SellingController {
    private final SellingService sellingService;

    public SellingController(SellingService sellingService) {
        this.sellingService = sellingService;
    }


    @PostMapping("makesale")
    public ResponseEntity<String> makeSale(@RequestBody SaleRequestBody body){
        return new ResponseEntity<>(sellingService.makeSale(body.getSaleRequests(), body.getPaymentMethod()),
                HttpStatus.OK);
    }
}
