package com.toyota.report.controller;

import com.toyota.report.entity.Sale;
import com.toyota.report.service.SaleListingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

    @GetMapping("paged")
    public ResponseEntity<Page<Sale>> getAllPaged(@RequestParam(value="pageNo",defaultValue = "0",required = false) int pageNo,
                                                  @RequestParam(value="pageSize",defaultValue = "1",required = false) int pageSize){
        return new ResponseEntity<>(saleListingService.getAllSales(pageNo, pageSize),HttpStatus.OK);
    }

    @GetMapping("sorted")
    public ResponseEntity<List<Sale>> getAllSorted(@RequestParam String sortBy, @RequestParam String sortOrder){
        return new ResponseEntity<>(saleListingService.getAllSortedSales(sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("createbill")
    public void createBillForSale(HttpServletResponse response, @RequestParam String billId) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        saleListingService.createBillForSale(response, billId);
    }

}
