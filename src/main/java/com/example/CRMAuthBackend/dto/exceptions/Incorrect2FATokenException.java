package com.example.CRMAuthBackend.dto.exceptions;

public class Incorrect2FATokenException extends Exception {
    public Incorrect2FATokenException(String message) {
        super(message);
    }
}
