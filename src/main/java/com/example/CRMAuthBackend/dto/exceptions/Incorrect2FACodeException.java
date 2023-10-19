package com.example.CRMAuthBackend.dto.exceptions;

public class Incorrect2FACodeException extends Exception{
    public Incorrect2FACodeException(String message) {
        super(message);
    }
}
