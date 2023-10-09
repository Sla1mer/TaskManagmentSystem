package com.example.PracticeAuthBackend.controllers;

import com.example.PracticeAuthBackend.config.UserAuthenticationProvider;
import com.example.PracticeAuthBackend.dto.auth.TokensDto;
import com.example.PracticeAuthBackend.dto.entities.UserDto;
import com.example.PracticeAuthBackend.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@RestController
@Tag(name = "AuthenticationController", description = "Авторизация/регистрация пользователей")
@RequestMapping("/api")
public class AuthenticationController {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService, UserAuthenticationProvider userAuthenticationProvider, AuthenticationService authenticationService1) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.authenticationService = authenticationService1;
    }

    @Operation(summary = "Авторизация пользователя", description = "Нужно в body передать json с полями: login, password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - (Не правильный логин или пароль)", content = {
                    @Content(schema = @Schema(implementation = Void.class))
            })
    })
    @PostMapping("/signIn")
    public ResponseEntity<TokensDto> signIn(@AuthenticationPrincipal UserDto user) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userAuthenticationProvider.createToken(user.getLogin()));
    }

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - (Не правильно переданы данные)", content = {
                    @Content(schema = @Schema(implementation = Void.class))
            })
    })
    @PostMapping("/regUser")
    public ResponseEntity<UserDto> regUser(@RequestBody UserDto user) throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        return ResponseEntity.ok(authenticationService.registrationUser(user).get());
    }

    @Operation(summary = "Обновление access и refresh токенов", description = "Нужно в Header (Authorization) передать refresh token в таком формате: Bearer your_token_here")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - (Не правильный токен или истекло время жизни ключа)", content = {
                    @Content(schema = @Schema(implementation = Void.class))
            })
    })
    @PostMapping("/refreshTokens")
    public ResponseEntity<TokensDto> refreshTokens(HttpServletRequest request) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userAuthenticationProvider.createNewTokensByRefreshToken(request));
    }

}
