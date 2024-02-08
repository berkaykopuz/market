package com.toyota.selling.exception;

public class CampaignNotFoundException extends RuntimeException{
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
