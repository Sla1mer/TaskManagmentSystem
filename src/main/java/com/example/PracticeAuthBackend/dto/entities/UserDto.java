package com.example.PracticeAuthBackend.dto.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String login;
    private String password;
    private String name;
    private String surname;
    private String numberPhone;
    private String country;
    private String city;
    private String address;


    public UserDto() {
    }

    public UserDto(String login, String password, String name, String surname, String numberPhone, String country, String city, String address) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.numberPhone = numberPhone;
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
