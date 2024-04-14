package com.toyota.selling.service;

import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.entity.*;
import com.toyota.selling.exception.BadSaleRequestException;
import com.toyota.selling.exception.CampaignNotFoundException;
import com.toyota.selling.exception.ProductNotFoundException;
import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.ProductRepository;
import com.toyota.selling.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class SellingServiceTest {
    private SaleRepository saleRepository;
    private CampaignRepository campaignRepository;
    private ProductRepository productRepository;
    private SellingService sellingService;

    @BeforeEach
    void setUp() {
        saleRepository = Mockito.mock(SaleRepository.class);
        campaignRepository = Mockito.mock(CampaignRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);

        sellingService = new SellingService(saleRepository, campaignRepository, productRepository);
    }

    @Test
    void testMakeSale_whenRequestedProductIsAvailableAndRequestedAmountIsEqualOrLowerThanStockAndCampaignIsAvailable_shouldMakeSale() {
        List<SaleRequest> saleRequests = new ArrayList<>();
        SaleRequest request = new SaleRequest(1L, 1, 1L);
        saleRequests.add(request);

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String username = "testUser";

        Product product = new Product();
        product.setId(1L);
        product.setAmount(2);
        product.setCategory("GIDA");
        product.setUpdatedDate(LocalDateTime.now());
        product.setPrice(1.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(CampaignType.FLAT_DISCOUNT);
        campaign.setStartDate(LocalDateTime.now().minusDays(1));
        campaign.setEndDate(LocalDateTime.now().plusDays(1));
        campaign.setDiscountRate(10);

        Mockito.when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        String expected = "Sale is made.";
        String result = sellingService.makeSale(saleRequests, paymentMethod, username);

        assertEquals(expected, result);
        Mockito.verify(productRepository, times(1)).findById(anyLong());
        Mockito.verify(campaignRepository, times(1)).findById(anyLong());
    }

    @Test
    void testMakeSale_whenRequestedProductIsAvailableAndRequestedAmountIsEqualOrLowerThanStockAndCampaignIsAvailable_shouldMakeSale2() {
        List<SaleRequest> saleRequests = new ArrayList<>();
        SaleRequest request = new SaleRequest(1L, 1, 1L);
        saleRequests.add(request);

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String username = "testUser";

        Product product = new Product();
        product.setId(1L);
        product.setAmount(2);
        product.setCategory("GIDA");
        product.setUpdatedDate(LocalDateTime.now());
        product.setPrice(1.0);

        Campaign campaign = new Campaign();
        campaign.setCampaignType(CampaignType.BUY_TWO_GET_ONE_FREE);
        campaign.setStartDate(LocalDateTime.now().minusDays(1));
        campaign.setEndDate(LocalDateTime.now().plusDays(1));
        campaign.setDiscountRate(10);

        Mockito.when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        String expected = "Sale is made.";
        String result = sellingService.makeSale(saleRequests, paymentMethod, username);

        assertEquals(expected, result);
        Mockito.verify(productRepository, times(1)).findById(anyLong());
        Mockito.verify(campaignRepository, times(1)).findById(anyLong());
    }

    @Test
    void testMakeSale_whenProductNotFound_shouldThrowProductNotException(){
        List<SaleRequest> saleRequests = new ArrayList<>();
        SaleRequest request = new SaleRequest(1L, 1, 1L);
        saleRequests.add(request);

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String username = "testUser";

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());


        assertThrows(ProductNotFoundException.class, () -> {
            sellingService.makeSale(saleRequests, paymentMethod, username);
        });

    }

    @Test
    public void testMakeSale_whenRequestedAmountIsHigherThanStock_shouldThrowBadSaleRequestException() {
        List<SaleRequest> saleRequests = new ArrayList<>();
        SaleRequest request = new SaleRequest(1L, 3, 1L);
        saleRequests.add(request);

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        String username = "testUser";

        Product product = new Product();
        product.setAmount(2);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));


        assertThrows(BadSaleRequestException.class, () -> {
            sellingService.makeSale(saleRequests, paymentMethod, username);
        });
    }

    @Test
    public void testReturnTheSale_whenSaleIsFoundAndProductIsAvailable_shouldUpdateProductAmountsAndDeleteSale() {
        String billId = "testBillId";
        Sale sale = new Sale();
        ProductSale productSale = new ProductSale();
        sale.setProductSales(Set.of(productSale));

        Product product = new Product();
        productSale.setProduct(product);
        productSale.setSaledAmount(5);
        product.setAmount(10);

        when(saleRepository.findById(billId)).thenReturn(Optional.of(sale));

        String result = sellingService.returnTheSale(billId);

        assertEquals("Sale is returned with id: " + billId, result);
        Mockito.verify(productRepository, times(1)).save(product);
        Mockito.verify(saleRepository, times(1)).delete(sale);
    }
}