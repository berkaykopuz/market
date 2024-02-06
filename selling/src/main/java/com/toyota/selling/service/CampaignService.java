package com.toyota.selling.service;

import com.toyota.selling.dto.CampaignDto;
import com.toyota.selling.entity.Campaign;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.repository.CampaignRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public List<CampaignDto> getAllCampaigns(){
        return campaignRepository.findAll().stream().map(CampaignDto::convert).collect(Collectors.toList());
    }

    public CampaignDto createProduct(CampaignDto campaignDto){
        if (    campaignDto.name() == null ||
                campaignDto.discountQuantity() == null ||
                campaignDto.discountAmount() == null ||
                campaignDto.requiredQuantity() == null ||
                campaignDto.startDate() == null ||
                campaignDto.endDate() == null
        ) {
            throw new BadCampaignRequestException("Invalid campaign data. All fields are required.");
        }

        if (    campaignDto.name().isEmpty() ||
                campaignDto.discountQuantity() < 0||
                campaignDto.discountAmount() < 0 ||
                campaignDto.requiredQuantity() < 0
        ) {
            throw new BadCampaignRequestException("Invalid campaign data. All fields must be valid.");
        }


        Campaign campaign = new Campaign();
        campaign.setName(campaignDto.name());
        campaign.setDiscountQuantity(campaignDto.discountQuantity());
        campaign.setRequiredQuantity(campaignDto.requiredQuantity());
        campaign.setDiscountAmount(campaignDto.discountAmount());
        campaign.setStartDate(campaignDto.startDate());
        campaign.setEndDate(campaignDto.endDate());

        return CampaignDto.convert(campaignRepository.save(campaign));
    }

    public String deleteCampaign(Long campaignId) {
        if(campaignRepository.existsById(campaignId)){
            campaignRepository.deleteById(campaignId);
            return "Campaign deleted";
        }
        else{
            return "Campaign not found with id: " + campaignId;
        }
    }
}
