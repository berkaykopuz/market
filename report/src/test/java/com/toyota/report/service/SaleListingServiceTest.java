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
import org.springframework.data.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
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
    public void testGetSalesWithPaginationAndSortingByDate() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("field"));
        Page<Sale> sales = new PageImpl<>(new ArrayList<>());
        when(saleRepository.findBySaleDate(2022, 12, 31, pageable)).thenReturn(sales);

        Page<Sale> result = saleListingService.getSalesWithPaginationAndSortingByDate(0,
                10,
                "field",
                2022,
                12,
                31);

        assertEquals(sales, result);
    }

    @Test
    public void testGetSalesWithPaginationAndSortingByCashierName() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("field"));

        Page<Sale> sales = new PageImpl<>(new ArrayList<>());

        when(saleRepository.findByCashierName("cashier", pageable)).thenReturn(sales);

        Page<Sale> result = saleListingService.getSalesWithPaginationAndSortingByCashierName(0,
                10,
                "field",
                "cashier");

        assertEquals(sales, result);
    }

    @Test
    public void testGetAllSalesWithPaginationAndSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("field"));
        Page<Sale> sales = new PageImpl<>(new ArrayList<>());
        when(saleRepository.findAll(eq(pageable))).thenReturn(sales);

        Page<Sale> result = saleListingService.getAllSalesWithPaginationAndSorting(0,
                10,
                "field");

        assertEquals(sales, result);
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