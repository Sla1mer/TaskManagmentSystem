package com.example.CRMAuthBackend.dto.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;
    private String name;
    private String surname;
    private String numberPhone;
    private String country;
    private String city;
    private String address;
    private String password;

    @Column(columnDefinition = "boolean default false")
    private boolean is2FA;
}
