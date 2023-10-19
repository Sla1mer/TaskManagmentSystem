package com.example.CRMAuthBackend.dto.auth;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Codes2FADto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private String code;

    public Codes2FADto(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
