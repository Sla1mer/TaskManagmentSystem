package com.example.CRMAuthBackend.dto.auth;

import com.example.CRMAuthBackend.dto.entities.UserDto;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private UserDto userDto;
    private TokenPayloadDto tokenPayloadDto;
}
