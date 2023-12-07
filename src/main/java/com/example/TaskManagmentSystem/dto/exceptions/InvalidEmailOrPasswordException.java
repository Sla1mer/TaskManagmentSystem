package com.example.TaskManagmentSystem.dto.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class InvalidEmailOrPasswordException extends AccessDeniedException {

    public InvalidEmailOrPasswordException(String message) {
        super(message);
    }
}
