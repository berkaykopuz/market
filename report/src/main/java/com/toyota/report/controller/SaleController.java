package com.toyota.report.controller;

import com.toyota.report.entity.Sale;
import com.toyota.report.service.SaleListingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleListingService saleListingService;

    public SaleController(SaleListingService saleListingService) {
        this.saleListingService = saleListingService;
    }

    @GetMapping
    public ResponseEntity<List<Sale>> searchBySaleDate(@RequestParam int year,
                                                       @RequestParam int month,
                                                       @RequestParam int day){
        return new ResponseEntity<>(saleListingService.findBySaleDate(year, month, day), HttpStatus.OK);
    }
}
