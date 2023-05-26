package com.example.journalactionbackend.dto.auth;

public class CredentialsDto {
    private String login;
    private String password;

    public CredentialsDto() {
    }

    public CredentialsDto(String login, String password) {
        this.login = login;
        this.password = password;
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
