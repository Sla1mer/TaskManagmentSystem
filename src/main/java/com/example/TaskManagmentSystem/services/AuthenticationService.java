package com.example.TaskManagmentSystem.services;

import com.example.TaskManagmentSystem.dto.auth.CredentialsDto;
import com.example.TaskManagmentSystem.dto.entities.RefreshToken;
import com.example.TaskManagmentSystem.dto.entities.User;
import com.example.TaskManagmentSystem.dto.exceptions.InvalidEmailOrPasswordException;
import com.example.TaskManagmentSystem.repository.RefreshTokenDtoRepository;
import com.example.TaskManagmentSystem.repository.UserRepository;
import com.example.TaskManagmentSystem.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenDtoRepository refreshTokenDtoRepository;


    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Async
    public CompletableFuture<User> authenticate(CredentialsDto credentialsDto) throws NoSuchAlgorithmException, InvalidEmailOrPasswordException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        User user = userRepository.findByEmailAndPassword(credentialsDto.getEmail(), encodePassword);

        if (user == null) throw new InvalidEmailOrPasswordException("Не правильный логин или пароль");

        return CompletableFuture.completedFuture(user);
    }

    @Async
    public CompletableFuture<User> registrationUser(User credentialsDto) throws NoSuchAlgorithmException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        credentialsDto.setPassword(encodePassword);

        try {
            userRepository.save(credentialsDto);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Такой email уже существует");
        }

        return CompletableFuture.completedFuture(credentialsDto);
    }

    @Async
    public CompletableFuture<RefreshToken> findRefreshTokenByUserId(long userId) {
        return CompletableFuture.completedFuture(refreshTokenDtoRepository.findByUserId(userId));
    }

    @Async
    public CompletableFuture<RefreshToken> findRefreshTokenByToken(String token) {
        return CompletableFuture.completedFuture(refreshTokenDtoRepository.findByToken(token));
    }

    @Async
    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenDtoRepository.save(refreshToken);
    }

    @Async
    public CompletableFuture<User> findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        return CompletableFuture.completedFuture(user);
    }

}
