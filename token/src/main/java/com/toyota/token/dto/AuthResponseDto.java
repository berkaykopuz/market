package com.toyota.token.dto;

public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
