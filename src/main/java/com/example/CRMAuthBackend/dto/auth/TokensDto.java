package com.example.CRMAuthBackend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class TokensDto {
    private String accessToken;
    private String refreshToken;
    private String temporaryToken;
    private boolean is2FA;
}
