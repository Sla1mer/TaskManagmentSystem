package com.example.TaskManagmentSystem.dto.auth;

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
}
