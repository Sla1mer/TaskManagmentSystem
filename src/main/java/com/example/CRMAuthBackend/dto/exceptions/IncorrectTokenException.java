package com.example.CRMAuthBackend.dto.exceptions;

public class IncorrectTokenException extends Exception{

    public IncorrectTokenException(String message) {
        super(message);
    }
}
