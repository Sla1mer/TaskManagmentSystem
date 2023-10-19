package com.example.CRMAuthBackend.dto.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class InvalidEmailOrPasswordException extends AccessDeniedException {

    public InvalidEmailOrPasswordException(String message) {
        super(message);
    }
}
