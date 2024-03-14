package com.toyota.selling.service;

import com.toyota.selling.dto.SaleDto;
import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.entity.*;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.exception.BadSaleRequestException;
import com.toyota.selling.exception.CampaignNotFoundException;
import com.toyota.selling.exception.ProductNotFoundException;
import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.ProductRepository;
import com.toyota.selling.repository.SaleRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.toyota.selling.entity.CampaignType.BUY_TWO_GET_ONE_FREE;
import static com.toyota.selling.entity.CampaignType.FLAT_DISCOUNT;

@Service
public class SellingService {
    private static Logger logger = LogManager.getLogger(SellingService.class);
    private final SaleRepository saleRepository;
    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;


    public SellingService(SaleRepository saleRepository, CampaignRepository campaignRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
    }

    /**
     * Processes a sale based on the provided sale requests and payment method.
     *
     * @param saleRequests A list of SaleRequest objects representing the items to be sold.
     * @param paymentMethod The payment method to be used for the sale.
     * @param username The username of the cashier making the sale.
     * @return A string message indicating the result of the sale process.
     */
    public String makeSale(List<SaleRequest> saleRequests, PaymentMethod paymentMethod, String username){
        double totalPrice = 0;
        double paidPrice = 0;
        long campaignId;

        Sale sale = new Sale();
        Set<ProductSale> productSales = new HashSet<>();

        for(SaleRequest s : saleRequests){
            Product product = productRepository.findById(s.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Requested product has not found."));

            if(s.getRequestedAmount() > product.getAmount()){
                logger.warn("Requested amount must not be higher than available value.");
                throw new BadSaleRequestException("Requested amount must not be higher than available stock.");
            }

            ProductSale productSale = new ProductSale();
            productSale.setProduct(product);
            productSale.setSaledAmount(s.getRequestedAmount());
            productSale.setSale(sale);

            productSales.add(productSale);

            if(s.getCampaignId() == null){ //null check
                campaignId = -1;
            }else{
                campaignId = s.getCampaignId();
            }

            Optional<Campaign> campaign = campaignRepository.findById(campaignId);
            if(campaign.isPresent()){
                if(LocalDateTime.now().isAfter(campaign.get().getStartDate()) &&
                        LocalDateTime.now().isBefore(campaign.get().getEndDate())
                ){
                    switch(campaign.get().getCampaignType()){
                        case BUY_TWO_GET_ONE_FREE:
                            paidPrice += buyTwoGetOneForFree(s, product);
                            totalPrice += saleWithoutDiscount(s, product);
                            product.setAmount(product.getAmount() - s.getRequestedAmount());
                            break;

                        case FLAT_DISCOUNT:
                            paidPrice += flatDiscount(s, product, campaign.get());
                            totalPrice += saleWithoutDiscount(s, product);
                            product.setAmount(product.getAmount() - s.getRequestedAmount());
                            break;
                    }
                }
                else{
                    logger.warn("Requested campaign is not available now.");
                    throw new CampaignNotFoundException("Requested campaign is not available now."); //change
                }

            }
            else{
                paidPrice += saleWithoutDiscount(s, product);
                totalPrice = paidPrice;
                product.setAmount(product.getAmount() - s.getRequestedAmount());
            }
        }

        sale.setProductSales(productSales);
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaidPrice(paidPrice);
        sale.setTotalPrice(totalPrice);
        sale.setPaymentMethod(paymentMethod);
        sale.setCashierName(username);
        saleRepository.save(sale);

        logger.info("Requested sale has completed");

        return "Sale is made.";
    }
    /**
     * Calculates the price for a "Buy Two, Get One Free" sale.
     *
     * @param saleRequest The request containing the amount of product requested.
     * @param product The product being purchased.
     * @return The total price after applying the discount.
     */
    private double buyTwoGetOneForFree(SaleRequest saleRequest, Product product){
        double price = 0;

        int discountedAmount = saleRequest.getRequestedAmount() / 3;
        price += product.getPrice() * (saleRequest.getRequestedAmount() - discountedAmount);
         //minus from stock

        return price;
    }

    /**
     * Calculates the price for a sale with a flat discount rate.
     *
     * @param saleRequest The request containing the amount of product requested.
     * @param product The product being purchased.
     * @param campaign The campaign containing the discount rate.
     * @return The total price after applying the flat discount.
     */
    private double flatDiscount(SaleRequest saleRequest, Product product, Campaign campaign){
        double price = 0;
        price += (product.getPrice() * saleRequest.getRequestedAmount())
                *
                (100 - campaign.getDiscountRate()) / 100;

        return price;
    }

    /**
     * Calculates the price for a sale without any discounts.
     *
     * @param saleRequest The request containing the amount of product requested.
     * @param product The product being purchased.
     * @return The total price without any discounts.
     */
    private double saleWithoutDiscount(SaleRequest saleRequest, Product product){
        double price = 0;
        price += product.getPrice() * saleRequest.getRequestedAmount();

        return price;
    }
}
