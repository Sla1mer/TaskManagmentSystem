package com.example.TaskManagmentSystem.dto.exceptions;

public class EmailExistsException extends Exception{

    public EmailExistsException(String message) {
        super(message);
    }
}
