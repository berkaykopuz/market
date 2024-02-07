package com.toyota.selling.controller;

import com.toyota.selling.dto.CampaignDto;
import com.toyota.selling.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaign")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public ResponseEntity<List<CampaignDto>> getAllCampaigns(){
        return new ResponseEntity<>(campaignService.getAllCampaigns(), HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<CampaignDto> createCampaign(@RequestBody CampaignDto campaignDto){
        return new ResponseEntity<>(campaignService.createCampaign(campaignDto), HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteCampaign(@PathVariable("id") Long id){
        return new ResponseEntity<>(campaignService.deleteCampaign(id), HttpStatus.OK);
    }

}
