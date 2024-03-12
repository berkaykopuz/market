package com.toyota.report.service;

import com.toyota.report.entity.PaymentMethod;
import com.toyota.report.entity.Product;
import com.toyota.report.entity.ProductSale;
import com.toyota.report.entity.Sale;
import com.toyota.report.exception.SaleNotFoundException;
import com.toyota.report.repository.ProductSaleRepository;
import com.toyota.report.repository.SaleRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SaleListingServiceTest {
    private ProductSaleRepository productSaleRepository;
    private SaleRepository saleRepository;
    private SaleListingService saleListingService;

    @BeforeEach
    void setUp() {
        productSaleRepository = mock(ProductSaleRepository.class);
        saleRepository = mock(SaleRepository.class);

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
    public void testCreateBillForSale_whenSaleIsFound_shouldCreatePdfDocument() throws IOException {

        String billId = "123";
        Sale sale = new Sale();
        sale.setBillId(billId);
        sale.setCashierName("testCashier");
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        sale.setTotalPrice(100.0);
        sale.setPaidPrice(90.0);

        ProductSale productSale = new ProductSale();
        Product product = new Product();
        product.setId(1L);
        product.setName("testProduct");
        product.setPrice(50.0);
        productSale.setProduct(product);
        productSale.setSaledAmount(2);
        sale.setProductSales(Set.of(productSale));

        HttpServletResponse response = mock(HttpServletResponse.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }
        });

        when(saleRepository.findById(billId)).thenReturn(Optional.of(sale));

        assertDoesNotThrow(() -> {
            saleListingService.createBillForSale(response, billId);
        });
    }

    @Test
    public void testCreateBillForSale_whenSaleIsNotFound_shouldThrowSaleNotFoundException() throws IOException {
        String billId = "123";
        when(saleRepository.findById(billId)).thenThrow(new SaleNotFoundException("Sale not found with id: " + billId));

        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThrows(SaleNotFoundException.class, () -> {
            saleListingService.createBillForSale(response, billId);
        });
    }

}