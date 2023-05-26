package com.example.journalactionbackend.dto.auth;

public class TokensBodyDto {
    private String refreshToken;

    public TokensBodyDto() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
