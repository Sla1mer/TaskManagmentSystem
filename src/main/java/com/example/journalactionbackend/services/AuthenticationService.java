package com.example.journalactionbackend.services;

import com.example.journalactionbackend.dto.auth.CredentialsDto;
import com.example.journalactionbackend.dto.entities.RefreshTokenDto;
import com.example.journalactionbackend.dto.entities.UserDto;
import com.example.journalactionbackend.repo.RefreshTokenDtoRepo;
import com.example.journalactionbackend.repo.UserDtoRepo;
import com.example.journalactionbackend.utils.PasswordUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationService {

    private final UserDtoRepo userDtoRepo;
    private final RefreshTokenDtoRepo refreshTokenDtoRepo;

    public AuthenticationService(UserDtoRepo userDtoRepo, RefreshTokenDtoRepo refreshTokenDtoRepo) {
        this.userDtoRepo = userDtoRepo;
        this.refreshTokenDtoRepo = refreshTokenDtoRepo;
    }

    @Async
    public CompletableFuture<UserDto> authenticate(CredentialsDto credentialsDto) throws NoSuchAlgorithmException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        UserDto userDto = userDtoRepo.findByLoginAndPassword(credentialsDto.getLogin(), encodePassword);

        System.out.println(encodePassword);

        if (userDto == null) throw new RuntimeException("Invalid password or login");

        return CompletableFuture.completedFuture(userDto);
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
