package com.example.CRMAuthBackend.dto.exceptions;

import org.springframework.security.core.AuthenticationException;

public class NotActiveAccountException extends AuthenticationException {
    public NotActiveAccountException(String message) {
        super(message);
    }
}
