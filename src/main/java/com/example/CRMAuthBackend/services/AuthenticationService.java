package com.example.CRMAuthBackend.services;

import com.example.CRMAuthBackend.dto.auth.Codes2FADto;
import com.example.CRMAuthBackend.dto.auth.CredentialsDto;
import com.example.CRMAuthBackend.dto.entities.RefreshTokenDto;
import com.example.CRMAuthBackend.dto.entities.UserDto;
import com.example.CRMAuthBackend.dto.exceptions.InvalidEmailOrPasswordException;
import com.example.CRMAuthBackend.dto.exceptions.NotActiveAccountException;
import com.example.CRMAuthBackend.repo.Codes2FADtoRepo;
import com.example.CRMAuthBackend.repo.RefreshTokenDtoRepo;
import com.example.CRMAuthBackend.repo.UserDtoRepo;
import com.example.CRMAuthBackend.utils.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthenticationService {

    @Autowired
    private UserDtoRepo userDtoRepo;

    @Autowired
    private Codes2FADtoRepo codesDto;

    @Autowired
    private RefreshTokenDtoRepo refreshTokenDtoRepo;


    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Async
    public CompletableFuture<UserDto> authenticate(CredentialsDto credentialsDto) throws NoSuchAlgorithmException, NotActiveAccountException, InvalidEmailOrPasswordException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        UserDto userDto = userDtoRepo.findByEmailAndPassword(credentialsDto.getEmail(), encodePassword);

        if (userDto == null) throw new InvalidEmailOrPasswordException("Не правильный логин или пароль");

        return CompletableFuture.completedFuture(userDto);
    }

    @Async
    public CompletableFuture<UserDto> signIn2FA(HttpServletRequest request, Codes2FADto codes2FADto) throws NoSuchAlgorithmException, NotActiveAccountException, InvalidEmailOrPasswordException {

        String jwtToken;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        } else throw new RuntimeException("Incorrect temporary token");



        return CompletableFuture.completedFuture(new UserDto());
    }

    @Async
    public CompletableFuture<UserDto> registrationUser(UserDto credentialsDto) throws NoSuchAlgorithmException {
        String encodePassword = PasswordUtils.toHexString(PasswordUtils.getSHA(credentialsDto.getPassword()));

        credentialsDto.setPassword(encodePassword);

        userDtoRepo.save(credentialsDto);

        UserDto newUser = userDtoRepo.findByEmailAndPassword(credentialsDto.getNumberPhone(), encodePassword);

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
    public void saveCode2FA(Codes2FADto codes2FADto) {codesDto.save(codes2FADto);}

    @Async
    public CompletableFuture<Boolean> validate2FACode(String email, String code) {
        Codes2FADto codes2FADto = codesDto.findByEmailAndCode(email, code);

        if (codes2FADto == null) return CompletableFuture.completedFuture(false);
        codesDto.delete(codes2FADto);
        return CompletableFuture.completedFuture(true);
    }

    @Async
    public void delete2FACode(Codes2FADto codes2FADto) {
        codesDto.delete(codes2FADto);
    }

    @Async
    public CompletableFuture<UserDto> findByEmail(String email) {
        UserDto userDto = userDtoRepo.findByEmail(email);

        return CompletableFuture.completedFuture(userDto);
    }

}
