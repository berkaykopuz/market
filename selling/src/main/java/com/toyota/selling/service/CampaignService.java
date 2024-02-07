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

    public CampaignDto createCampaign(CampaignDto campaignDto){
        if (    campaignDto.name() == null ||
                campaignDto.campaignType() == null ||
                campaignDto.discountRate() == null ||
                campaignDto.startDate() == null ||
                campaignDto.endDate() == null
        ) {
            throw new BadCampaignRequestException("Invalid campaign data. All fields are required.");
        }

        if (    campaignDto.name().isEmpty() ||
                campaignDto.campaignType().name().isEmpty() ||
                campaignDto.discountRate() < 0 // maybe should add date check(mustn't before now)
        ) {
            throw new BadCampaignRequestException("Invalid campaign data. All fields must be valid.");
        }


        Campaign campaign = new Campaign();
        campaign.setName(campaignDto.name());
        campaign.setStartDate(campaignDto.startDate());
        campaign.setEndDate(campaignDto.endDate());
        campaign.setCampaignType(campaignDto.campaignType());
        campaign.setDiscountRate(campaignDto.discountRate());

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
