package com.toyota.report.service;

import com.toyota.report.entity.Product;
import com.toyota.report.entity.ProductSale;
import com.toyota.report.entity.Sale;
import com.toyota.report.repository.ProductSaleRepository;
import com.toyota.report.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleListingService {
    private final ProductSaleRepository productSaleRepository;
    private final SaleRepository saleRepository;


    public SaleListingService(ProductSaleRepository productSaleRepository, SaleRepository saleRepository) {
        this.productSaleRepository = productSaleRepository;
        this.saleRepository = saleRepository;
    }

    public List<Sale> findBySaleDate(int year, int month, int day){
        return saleRepository.findBySaleDate(year, month, day);
    }

    public Page<Sale> getAllSales(int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Sale> salePage = saleRepository.findAll(pageable);

        return salePage;
    }
}
