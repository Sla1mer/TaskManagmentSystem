package com.example.CRMAuthBackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensBodyDto {
    private String refreshToken;
}
