package com.toyota.selling.service;

import com.toyota.selling.repository.CampaignRepository;
import com.toyota.selling.repository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
public class SellingService {
    private final SaleRepository saleRepository;
    private final CampaignRepository campaignRepository;


    public SellingService(SaleRepository saleRepository, CampaignRepository campaignRepository) {
        this.saleRepository = saleRepository;
        this.campaignRepository = campaignRepository;
    }
}
