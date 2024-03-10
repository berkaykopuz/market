package com.toyota.report.service;

import com.toyota.report.entity.Sale;
import com.toyota.report.exception.SaleNotFoundException;
import com.toyota.report.repository.ProductSaleRepository;
import com.toyota.report.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SaleListingServiceTest {
    private ProductSaleRepository productSaleRepository;
    private SaleRepository saleRepository;
    private SaleListingService saleListingService;

    @BeforeEach
    void setUp() {
        productSaleRepository = Mockito.mock(ProductSaleRepository.class);
        saleRepository = Mockito.mock(SaleRepository.class);

        saleListingService = new SaleListingService (productSaleRepository, saleRepository);
    }

    @Test
    void testFindBySaleDate_whenRequestIsValid_shouldReturnSalesWithSpecificDate() {

        int year = 2022;
        int month = 12;
        int day = 31;
        List<Sale> expectedSales = new ArrayList<>();
        when(saleRepository.findBySaleDate(year, month, day)).thenReturn(expectedSales);

        List<Sale> result = saleListingService.findBySaleDate(year, month, day);

        assertEquals(expectedSales, result);
    }

    @Test
    void testGetAllSales_whenPageRequestIsValid_shouldReturnAllDates() {
        int pageNo = 0;
        int pageSize = 2;
        Page<Sale> expectedPage = new PageImpl<>(new ArrayList<>());

        when(saleRepository.findAll(PageRequest.of(pageNo, pageSize))).thenReturn(expectedPage);

        Page<Sale> result = saleListingService.getAllSales(pageNo, pageSize);

        assertEquals(expectedPage, result);
    }

    @Test
    void testGetAllSortedSales_whenSortUrlRequestIsValidAndAscend_shouldReturnAllSalesByRequestedOrder() {
        String sortBy = "date";
        String sortOrder = "asc";
        List<Sale> expectedSales = new ArrayList<>();
        when(saleRepository.findAll(Sort.by(sortBy).ascending())).thenReturn(expectedSales);

        List<Sale> result = saleListingService.getAllSortedSales(sortBy, sortOrder);

        assertEquals(expectedSales, result);
    }

    @Test
    void testGetAllSortedSales_whenSortUrlRequestIsValidAndDescend_shouldReturnAllSalesByRequestedOrder() {
        String sortBy = "date";
        String sortOrder = "desc";
        List<Sale> expectedSales = new ArrayList<>();
        when(saleRepository.findAll(Sort.by(sortBy).ascending())).thenReturn(expectedSales);

        List<Sale> result = saleListingService.getAllSortedSales(sortBy, sortOrder);

        assertEquals(expectedSales, result);
    }

    @Test
    void testGetSale_whenSaleIsAvailable_shouldReturnSale() {
        String billId = "123";
        Sale expectedSale = new Sale();
        when(saleRepository.findById(billId)).thenReturn(Optional.of(expectedSale));

        Sale result = saleListingService.getSale(billId);

        assertEquals(expectedSale, result);
    }

    @Test
    void testGetSale_whenSaleIsNotFound_shouldThrowSaleNotFoundException() {
        String billId = "123";

        when(saleRepository.findById(billId)).thenThrow(new SaleNotFoundException("Sale not found with id: " + billId));


        assertThrows(SaleNotFoundException.class, () ->{
            saleListingService.getSale(billId);
        });
    }

    @Test
    void createBillForSale() {
    }
}