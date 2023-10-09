package com.example.PracticeAuthBackend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.PracticeAuthBackend.dto.auth.CredentialsDto;
import com.example.PracticeAuthBackend.dto.auth.TokenPayloadDto;
import com.example.PracticeAuthBackend.dto.auth.TokensDto;
import com.example.PracticeAuthBackend.dto.entities.RefreshTokenDto;
import com.example.PracticeAuthBackend.dto.entities.UserDto;
import com.example.PracticeAuthBackend.repo.RefreshTokenDtoRepo;
import com.example.PracticeAuthBackend.repo.UserDtoRepo;
import com.example.PracticeAuthBackend.utils.PasswordUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationService {

    private final UserDtoRepo userDtoRepo;
    private final RefreshTokenDtoRepo refreshTokenDtoRepo;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final Gson gson;

    public AuthenticationService(UserDtoRepo userDtoRepo, RefreshTokenDtoRepo refreshTokenDtoRepo, Gson gson) {
        this.userDtoRepo = userDtoRepo;
        this.refreshTokenDtoRepo = refreshTokenDtoRepo;
        this.gson = gson;
    }

    @Async
    public CompletableFuture<UserDto> authenticate(CredentialsDto credentialsDto) throws NoSuchAlgorithmException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        UserDto userDto = userDtoRepo.findByLoginAndPassword(credentialsDto.getLogin(), encodePassword);

        if (userDto == null) throw new RuntimeException("Invalid password or login");

        return CompletableFuture.completedFuture(userDto);
    }

    @Async
    public CompletableFuture<UserDto> registrationUser(UserDto credentialsDto) throws NoSuchAlgorithmException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        UserDto user = new UserDto(credentialsDto.getLogin(), encodePassword);

        userDtoRepo.save(user);

        UserDto newUser = userDtoRepo.findByLoginAndPassword(credentialsDto.getLogin(), encodePassword);

        return CompletableFuture.completedFuture(newUser);
    }

    @Async
    public CompletableFuture<RefreshTokenDto> findRefreshTokenByUserId(long userId) {
        return CompletableFuture.completedFuture(refreshTokenDtoRepo.findByUserId(userId));
    }

    @Async
    public CompletableFuture<RefreshTokenDto> findRefreshTokenByToken(String token) {
        return CompletableFuture.completedFuture(refreshTokenDtoRepo.findByToken(token));
    }

    @Async
    public void saveRefreshToken(RefreshTokenDto refreshTokenDto) {
        refreshTokenDtoRepo.save(refreshTokenDto);
    }

    @Async
    public CompletableFuture<UserDto> findByLogin(String login) {
        UserDto userDto = userDtoRepo.findByLogin(login);

        if (userDto == null) throw new RuntimeException("Invalid login");

        return CompletableFuture.completedFuture(userDto);
    }

}
