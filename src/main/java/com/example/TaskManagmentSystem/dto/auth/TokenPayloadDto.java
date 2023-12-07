package com.example.TaskManagmentSystem.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenPayloadDto {
    private String typeToken;
}
