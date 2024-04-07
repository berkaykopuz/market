package com.toyota.selling.controller;

import com.toyota.selling.dto.SaleRequestBody;
import com.toyota.selling.service.JwtUtil;
import com.toyota.selling.service.SellingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/selling")
public class SellingController {
    private final SellingService sellingService;
    private final JwtUtil jwtUtil;
    public SellingController(SellingService sellingService, JwtUtil jwtUtil) {
        this.sellingService = sellingService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("makesale")
    public ResponseEntity<String> makeSale(@RequestBody SaleRequestBody body, HttpServletRequest request){
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader = authHeader.substring(7);
        }
        String username = jwtUtil.getUsernameFromToken(authHeader);

        return new ResponseEntity<>(sellingService.makeSale(body.getSaleRequests(), body.getPaymentMethod(), username),
                HttpStatus.OK);
    }

    @DeleteMapping("delete/{billId}")
    public ResponseEntity<String> returnTheSale(@PathVariable("billId") String billId){
        return new ResponseEntity<>(sellingService.returnTheSale(billId), HttpStatus.OK);
    }
}
