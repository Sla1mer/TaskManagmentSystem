package com.example.TaskManagmentSystem.controllers;

import com.example.TaskManagmentSystem.dto.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

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

    // Такой пользователь уже существует в системе
    @ExceptionHandler({ DataIntegrityViolationException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleDataIntegrityViolationExceptionException(DataIntegrityViolationException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
    }

    // Сущность не найдена
    @ExceptionHandler({ EntityNotFoundException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleEntityNotFoundExceptionException(EntityNotFoundException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // Вы не имеете права изменять статус этой задачи
    @ExceptionHandler({ AccessDeniedException.class })
    @ResponseBody
    public ResponseEntity<ErrorDto> handleAccessDeniedExceptionException(AccessDeniedException ex) {

        ErrorDto errorDto = new ErrorDto(ex.getMessage());
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

}
