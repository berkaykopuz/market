package com.toyota.report.controller;

import com.toyota.report.entity.Sale;
import com.toyota.report.service.SaleListingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public ResponseEntity<Page<Sale>> getAllPaged(@RequestParam(value="pageNo",defaultValue = "0") int pageNo,
                                                  @RequestParam(value="pageSize",defaultValue = "1") int pageSize,
                                                  @RequestParam String field,
                                                  @RequestParam(required = false) Integer year,
                                                  @RequestParam(required = false) Integer month,
                                                  @RequestParam(required = false) Integer day,
                                                  @RequestParam(required = false) String cashierName){
        if(cashierName != null){
            return new ResponseEntity<>(saleListingService.getSalesWithPaginationAndSortingByCashierName(pageNo,
                    pageSize,
                    field,
                    cashierName) ,HttpStatus.OK);
        }else if(year != null && month != null && day != null){
            return new ResponseEntity<>(saleListingService.getSalesWithPaginationAndSortingByDate(pageNo,
                    pageSize,
                    field,
                    year,
                    month,
                    day) ,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(saleListingService.getAllSalesWithPaginationAndSorting(pageNo, pageSize, field),
                    HttpStatus.OK);
        }

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
