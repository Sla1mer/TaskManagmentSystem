package com.example.PracticeAuthBackend.controllers;

import com.example.PracticeAuthBackend.config.UserAuthenticationProvider;
import com.example.PracticeAuthBackend.dto.auth.TokensDto;
import com.example.PracticeAuthBackend.dto.entities.UserDto;
import com.example.PracticeAuthBackend.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final UserAuthenticationProvider userAuthenticationProvider;

    public AuthenticationController(AuthenticationService authenticationService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @PostMapping("/signIn")
    public ResponseEntity<TokensDto> signIn(@AuthenticationPrincipal UserDto user) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userAuthenticationProvider.createToken(user.getLogin()));
    }

    @PostMapping("/refreshTokens")
    public ResponseEntity<TokensDto> refreshTokens(@AuthenticationPrincipal UserDto user) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(userAuthenticationProvider.createToken(user.getLogin()));
    }

}
