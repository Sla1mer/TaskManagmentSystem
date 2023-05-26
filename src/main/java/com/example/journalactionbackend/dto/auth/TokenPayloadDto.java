package com.example.journalactionbackend.dto.auth;

public class TokenPayloadDto {
    private String typeToken;

    public TokenPayloadDto() {
    }

    public TokenPayloadDto(String typeToken) {
        this.typeToken = typeToken;
    }

    public String getTypeToken() {
        return typeToken;
    }

    public void setTypeToken(String typeToken) {
        this.typeToken = typeToken;
    }
}
