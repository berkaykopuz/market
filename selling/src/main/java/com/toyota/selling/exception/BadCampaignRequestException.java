package com.toyota.selling.exception;

public class BadCampaignRequestException extends RuntimeException{
    public BadCampaignRequestException(String message) {
        super(message);
    }
}
