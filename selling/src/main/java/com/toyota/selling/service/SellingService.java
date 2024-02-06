package com.toyota.selling.service;

import com.toyota.selling.dto.SaleRequest;
import com.toyota.selling.entity.Product;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.ProductRepository;
import com.toyota.selling.repository.SaleRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
        double totalAmount = 0;

        for(SaleRequest s : saleRequests){
            Product product = productRepository.findById(s.getProductId())
                    .orElseThrow(() -> new NotFoundException("")); //change

            if(s.getRequestedQuantity() > product.getAmount())

            if(campaignRepository.existsById(s.getCampaignId())){

            }
            else{
                throw new BadCampaignRequestException(""); //change to not found
            }
        }
    }

    private int buyTwoGetOneForFree(saleR)
}
