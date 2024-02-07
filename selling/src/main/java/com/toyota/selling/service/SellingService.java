package com.toyota.selling.service;

import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.entity.Campaign;
import com.toyota.selling.entity.Product;
import com.toyota.selling.entity.Sale;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.ProductRepository;
import com.toyota.selling.repository.SaleRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public String makeSale(List<SaleRequest> saleRequests){
        double totalPrice = 0;
        double paidPrice = 0;
        Sale sale = new Sale();

        for(SaleRequest s : saleRequests){
            Product product = productRepository.findById(s.getProductId())
                    .orElseThrow(() -> new NotFoundException("")); //change

            if(s.getRequestedAmount() > product.getAmount()){
                throw new BadRequestException(); //change
            }

            sale.getProducts().add(product);

            Optional<Campaign> campaign = campaignRepository.findById(s.getCampaignId());
            if(campaign.isPresent()){
                switch(campaign.get().getCampaignType()){
                    case BUY_TWO_GET_ONE_FREE:
                        paidPrice += buyTwoGetOneForFree(s, product);
                        totalPrice += saleWithoutDiscount(s, product);

                    case FLAT_DISCOUNT:

                        totalPrice += saleWithoutDiscount(s, product);

                }
            }
            else{
                paidPrice += saleWithoutDiscount(s, product);
                totalPrice += paidPrice;
            }
        }


    }

    private double buyTwoGetOneForFree(SaleRequest saleRequest, Product product){
        double price = 0;

        int discountedAmount = saleRequest.getRequestedAmount() / 3;
        price += product.getPrice() * (saleRequest.getRequestedAmount() - discountedAmount);
        return price;
    }

    private double flatDiscount(SaleRequest saleRequest, Product product, Campaign campaign){
        double price = 0;

        price += (product.getPrice() * saleRequest.getRequestedAmount()) * (100 - campaign.getDiscountRate()) / 100;

        return price;
    }

    private double saleWithoutDiscount(SaleRequest saleRequest, Product product){
        double price = 0;

        price += product.getPrice() * saleRequest.getRequestedAmount();
        return price;
    }
}
