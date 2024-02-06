package com.toyota.selling.service;

import com.toyota.selling.repository.CampaignRepository;

public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }


}
