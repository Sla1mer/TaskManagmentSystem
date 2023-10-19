package com.example.CRMAuthBackend.controllers;

import com.example.CRMAuthBackend.dto.exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    private final Logger logger = LogManager.getLogger(ExceptionHandlerController.class);

    // Не правильный логин или пароль
    @ExceptionHandler(InvalidEmailOrPasswordException.class)
    @ResponseBody
    public final ResponseEntity<ErrorDto> handleInvalidAuthExceptions(InvalidEmailOrPasswordException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

    // Не активированный аккаунт
    @ExceptionHandler(NotActiveAccountException.class)
    public final ResponseEntity<ErrorDto> handleNotActiveAccountExceptions(NotActiveAccountException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

    // Не правильный код
    @ExceptionHandler({ Incorrect2FACodeException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleIncorrect2FACodeException(Incorrect2FACodeException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Не правильный временный токен
    @ExceptionHandler({ Incorrect2FATokenException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleIncorrect2FATokenException(Incorrect2FATokenException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Не правильный токен
    @ExceptionHandler({ IncorrectTokenException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleIncorrectTokenException(IncorrectTokenException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Такой email уже существует
    @ExceptionHandler({ EmailExistsException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleEmailExistsException(EmailExistsException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Вы не авторизовались
    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

}
