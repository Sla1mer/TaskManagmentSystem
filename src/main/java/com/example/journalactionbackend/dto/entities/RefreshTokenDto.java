package com.example.journalactionbackend.dto.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "refresh_token")
public class RefreshTokenDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDto user;

    public RefreshTokenDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
