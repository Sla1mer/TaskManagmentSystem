package com.example.journalactionbackend.enums;

public enum TokenType {
    ACCESS(1, "access"),
    REFRESH(2, "refresh");

    private final int tokenTypeId;
    private final String tokenType;

    TokenType(int tokenTypeId, String tokenType) {
        this.tokenTypeId = tokenTypeId;
        this.tokenType = tokenType;
    }

    public int getTokenTypeId() {
        return tokenTypeId;
    }

    public String getTokenType() {
        return tokenType;
    }
}
