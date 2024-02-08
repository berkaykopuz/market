package com.toyota.selling.service;

import com.toyota.selling.dto.SaleDto;
import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.entity.Campaign;
import com.toyota.selling.entity.PaymentMethod;
import com.toyota.selling.entity.Product;
import com.toyota.selling.entity.Sale;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.exception.BadSaleRequestException;
import com.toyota.selling.exception.CampaignNotFoundException;
import com.toyota.selling.exception.ProductNotFoundException;
import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.ProductRepository;
import com.toyota.selling.repository.SaleRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.toyota.selling.entity.CampaignType.BUY_TWO_GET_ONE_FREE;
import static com.toyota.selling.entity.CampaignType.FLAT_DISCOUNT;

@Service
public class SellingService {
    private final SaleRepository saleRepository;
    private final CampaignRepository campaignRepository;
    private final ProductRepository productRepository;


    public SellingService(SaleRepository saleRepository, CampaignRepository campaignRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
    }

    public String makeSale(List<SaleRequest> saleRequests, PaymentMethod paymentMethod){
        double totalPrice = 0;
        double paidPrice = 0;
        Sale sale = new Sale();
        Set<Product> products = new HashSet<>();

        for(SaleRequest s : saleRequests){
            Product product = productRepository.findById(s.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Requested product has not found.")); //change

            if(s.getRequestedAmount() > product.getAmount()){
                throw new BadSaleRequestException("Requested amount must not pass available value."); //change
            }

            products.add(product);

            Optional<Campaign> campaign = campaignRepository.findById(s.getCampaignId());
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
                    throw new CampaignNotFoundException("Requested campaign not found."); //change
                }

            }
            else{
                paidPrice += saleWithoutDiscount(s, product);
                totalPrice += paidPrice;
                product.setAmount(product.getAmount() - s.getRequestedAmount());
            }
        }

        sale.setProducts(products);
        sale.setSaleDate(LocalDateTime.now());
        sale.setPaidPrice(paidPrice);
        sale.setTotalPrice(totalPrice);
        sale.setPaymentMethod(paymentMethod);
        saleRepository.save(sale);

        return "Sale is made.";
    }

    private double buyTwoGetOneForFree(SaleRequest saleRequest, Product product){
        double price = 0;

        int discountedAmount = saleRequest.getRequestedAmount() / 3;
        price += product.getPrice() * (saleRequest.getRequestedAmount() - discountedAmount);
         //minus from stock

        return price;
    }

    private double flatDiscount(SaleRequest saleRequest, Product product, Campaign campaign){
        double price = 0;
        price += (product.getPrice() * saleRequest.getRequestedAmount())
                *
                (100 - campaign.getDiscountRate()) / 100;

        return price;
    }

    private double saleWithoutDiscount(SaleRequest saleRequest, Product product){
        double price = 0;
        price += product.getPrice() * saleRequest.getRequestedAmount();

        return price;
    }

    public List<SaleDto> getAllSales(){
        return saleRepository.findAll().stream().map(SaleDto::convert).collect(Collectors.toList());
    }
}
