package com.toyota.selling.service;

import com.toyota.selling.dto.CampaignDto;
import com.toyota.selling.entity.Campaign;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.repository.CampaignRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private static Logger logger = LogManager.getLogger(CampaignService.class);
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    /**
     * Retrieves all campaigns from the repository and returns them as a list of CampaignDto.
     *
     * @return A list of CampaignDto representing all campaigns.
     */
    public List<CampaignDto> getAllCampaigns(){
        logger.info("Getting all campaigns");
        return campaignRepository.findAll().stream().map(CampaignDto::convert).collect(Collectors.toList());
    }

    /**
     * Creates a new campaign with the given campaign data.
     *
     * @param campaignDto The data transfer object containing campaign details.
     * @return The CampaignDto of the saved campaign.
     * @throws BadCampaignRequestException If any required fields are missing, invalid, or if the discount rate is not within the valid range, or if the campaign dates are not in the correct order or before the current time.
     */
    public CampaignDto createCampaign(CampaignDto campaignDto){
        logger.debug("Received request to create campaign");

        if (    campaignDto.name() == null ||
                campaignDto.campaignType() == null ||
                campaignDto.discountRate() == null ||
                campaignDto.startDate() == null ||
                campaignDto.endDate() == null
        ) {
            logger.warn("Invalid campaign data. All fields are required.");
            throw new BadCampaignRequestException("Invalid campaign data. All fields are required.");
        }

        if (    campaignDto.name().isEmpty() ||
                campaignDto.campaignType().name().isEmpty() ||
                campaignDto.discountRate() < 0 // maybe should add date check(mustn't before now)
        ) {
            logger.warn("Invalid campaign data. All fields must be valid.");
            throw new BadCampaignRequestException("Invalid campaign data. All fields must be valid.");
        }

        if(campaignDto.discountRate() > 100){
            logger.warn("Discount rate could not higher than 100 percent");
            throw new BadCampaignRequestException("Discount rate must not higher than 100 percent");
        }

        if(campaignDto.endDate().isBefore(campaignDto.startDate()) ||
            campaignDto.endDate().isBefore(LocalDateTime.now().plusHours(3))){

            logger.warn("Campaign dates must not before now");
            throw new BadCampaignRequestException("Campaign dates must not before now");
        }

        logger.info("Creating new campaign object");
        Campaign campaign = new Campaign();
        campaign.setName(campaignDto.name());
        campaign.setStartDate(campaignDto.startDate());
        campaign.setEndDate(campaignDto.endDate());
        campaign.setCampaignType(campaignDto.campaignType());
        campaign.setDiscountRate(campaignDto.discountRate());

        logger.info("Saving campaign object to the repository");
        return CampaignDto.convert(campaignRepository.save(campaign));
    }

    /**
     * Deletes a campaign from the repository by its ID.
     *
     * @param campaignId The ID of the campaign to be deleted.
     * @return A string message indicating whether the campaign was successfully deleted or not found.
     */
    public String deleteCampaign(Long campaignId) {
        if(campaignRepository.existsById(campaignId)){
            logger.info("Campaign deleted");
            campaignRepository.deleteById(campaignId);
            return "Campaign deleted";
        }
        else{
            logger.warn("Campaign not found with id: " + campaignId);
            return "Campaign not found with id: " + campaignId;
        }
    }
}
