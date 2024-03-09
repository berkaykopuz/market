package com.toyota.selling.service;

import com.toyota.selling.dto.CampaignDto;
import com.toyota.selling.entity.Campaign;
import com.toyota.selling.entity.CampaignType;
import com.toyota.selling.exception.BadCampaignRequestException;
import com.toyota.selling.repository.CampaignRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CampaignServiceTest {

    private CampaignRepository campaignRepository;
    private CampaignService campaignService;
    private MockedStatic<CampaignDto> mockStatic;

    @BeforeEach
    void setUp() {
        campaignRepository = Mockito.mock(CampaignRepository.class);
        mockStatic = Mockito.mockStatic(CampaignDto.class);

        campaignService = new CampaignService(campaignRepository);
    }

    @Test
    void testGetAllCampaigns_whenCampaignIsAvailable_shouldReturnCampaignsWithCampaignDto() {
        List<Campaign> campaigns = new ArrayList<>();
        Campaign campaign = generateCampaign();
        campaigns.add(campaign);
        CampaignDto campaignDto = generateCampaignDto(campaign);
        List<CampaignDto> expected = new ArrayList<>();
        expected.add(campaignDto);

        when(campaignRepository.findAll()).thenReturn(campaigns);
        when(CampaignDto.convert(campaign)).thenReturn(campaignDto);

        List<CampaignDto> result = campaignService.getAllCampaigns();

        assertEquals(expected, result);
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void testCreateCampaign_whenCampaignRequestIsNotNullandAvailable_shouldSaveCampaignandReturnCampaignDto() {
        Campaign campaign = generateCampaign();
        CampaignDto expected = generateCampaignDto(campaign);

        when(CampaignDto.convert(campaignRepository.save(campaign))).thenReturn(expected);

        CampaignDto result = campaignService.createCampaign(expected);

        assertEquals(expected, result);

        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    void testCreateCampaign_whenCampaignRequestIsNull_shouldThrowBadCampaignRequestException() {
        CampaignDto campaignDto = new CampaignDto(null, null, null, null, null,
                null);

        assertThrows(BadCampaignRequestException.class, () -> campaignService.createCampaign(campaignDto));

        Mockito.verifyNoInteractions(campaignRepository);
    }

    @Test
    void testCreateCampaign_whenCampaignRequestIsNotValid_shouldThrowBadCampaignRequestException() {
        CampaignDto campaignDto = new CampaignDto(1L,
                "",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                CampaignType.FLAT_DISCOUNT,
                -10);

        assertThrows(BadCampaignRequestException.class, () -> campaignService.createCampaign(campaignDto));

        Mockito.verifyNoInteractions(campaignRepository);
    }

    @Test
    void testCreateCampaign_whenDiscountRateRequestIsHigherThanHundredPercent_shouldThrowBadCampaignRequestException() {
        CampaignDto campaignDto = new CampaignDto(1L,
                "Kampanya",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                CampaignType.FLAT_DISCOUNT,
                200);

        assertThrows(BadCampaignRequestException.class, () -> campaignService.createCampaign(campaignDto));

        Mockito.verifyNoInteractions(campaignRepository);
    }

    @Test
    void testDeleteUser_whenProductExist_shouldDeleteProduct() {
        Long campaignId = 1L;

        when(campaignRepository.existsById(campaignId)).thenReturn(true);
        doNothing().when(campaignRepository).deleteById(campaignId);

        String result = campaignService.deleteCampaign(campaignId);
        String expected = "Campaign deleted";

        assertEquals(expected, result);
    }

    @Test
    void testDeleteCampaign_whenCampaignIsNotExist_shouldReturnExceptionMessage() {
        Long campaignId = 1L;

        when(campaignRepository.existsById(campaignId)).thenReturn(false);


        String result = campaignService.deleteCampaign(campaignId);
        String expected = "Campaign not found with id: " + campaignId;

        assertEquals(expected, result);
    }


    private Campaign generateCampaign(){
        Campaign campaign = new Campaign();

        campaign.setId(1L);
        campaign.setName("product");
        campaign.setStartDate(LocalDateTime.now().minusDays(1));
        campaign.setEndDate(LocalDateTime.now().plusDays(1));
        campaign.setCampaignType(CampaignType.FLAT_DISCOUNT);
        campaign.setDiscountRate(10);

        return campaign;
    }

    private CampaignDto generateCampaignDto(Campaign campaign){

        CampaignDto campaignDto = new CampaignDto(
                1L,
                campaign.getName(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getCampaignType(),
                campaign.getDiscountRate()
        );

        return campaignDto;
    }

    @AfterEach
    public void afterEach() {
        mockStatic.close();
    }
}