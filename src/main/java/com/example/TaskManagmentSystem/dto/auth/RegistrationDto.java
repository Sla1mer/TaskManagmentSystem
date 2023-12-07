package com.example.TaskManagmentSystem.dto.auth;

import com.example.TaskManagmentSystem.dto.entities.User;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationDto {
    private User user;
    private TokenPayloadDto tokenPayloadDto;
}
