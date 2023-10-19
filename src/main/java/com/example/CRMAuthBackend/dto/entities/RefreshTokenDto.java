package com.example.CRMAuthBackend.dto.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "refresh_token")
@Data
public class RefreshTokenDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;
}
