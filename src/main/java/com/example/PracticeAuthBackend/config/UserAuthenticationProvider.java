package com.example.PracticeAuthBackend.config;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.PracticeAuthBackend.dto.auth.CredentialsDto;
import com.example.PracticeAuthBackend.dto.auth.TokenPayloadDto;
import com.example.PracticeAuthBackend.dto.auth.TokensDto;
import com.example.PracticeAuthBackend.dto.entities.RefreshTokenDto;
import com.example.PracticeAuthBackend.dto.entities.UserDto;
import com.example.PracticeAuthBackend.services.AuthenticationService;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final AuthenticationService authenticationService;
    private final Gson gson;

    public UserAuthenticationProvider(AuthenticationService authenticationService, Gson gson) {
        this.authenticationService = authenticationService;
        this.gson = gson;
    }

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokensDto createToken(String login) throws ExecutionException, InterruptedException {
        Date now = new Date();
        Date validityAccess = new Date(now.getTime() + 1800000); // 30 minutes (access token)
        Date validityRefresh = new Date(now.getTime() + 604800000); // 7 days (refresh token)


        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String accessToken = JWT.create()
                .withIssuer("Practice")
                .withSubject(login)
                .withIssuedAt(now)
                .withExpiresAt(validityAccess)
                .withPayload(gson.toJson(new TokenPayloadDto("access")))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuer("Practice")
                .withSubject(login)
                .withIssuedAt(now)
                .withPayload(gson.toJson(new TokenPayloadDto("refresh")))
                .withExpiresAt(validityRefresh)
                .sign(algorithm);


        UserDto userDto = authenticationService.findByLogin(login).get();
        RefreshTokenDto refreshTokenDto = authenticationService.findRefreshTokenByUserId(userDto.getId()).get();

        if (authenticationService.findRefreshTokenByUserId(userDto.getId()).get() == null) {
            refreshTokenDto = new RefreshTokenDto();
            refreshTokenDto.setUser(authenticationService.findByLogin(login).get());
        }

        refreshTokenDto.setToken(refreshToken);
        authenticationService.saveRefreshToken(refreshTokenDto);

        return new TokensDto(accessToken, refreshToken);
    }

    public TokensDto createNewTokensByRefreshToken(HttpServletRequest request) throws ExecutionException, InterruptedException {

        String jwtToken;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        } else throw new RuntimeException("Incorrect refresh token");

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(jwtToken);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        TokenPayloadDto tokenPayloadDto = gson.fromJson(decodedString, TokenPayloadDto.class);

        RefreshTokenDto refreshTokenDto = authenticationService.findRefreshTokenByToken(jwtToken).get();

        if (tokenPayloadDto.getTypeToken().equals("refresh")) {
            if (refreshTokenDto == null) throw new RuntimeException("Incorrect refresh token");

            return createToken(refreshTokenDto.getUser().getLogin());
        } else  {
            throw new RuntimeException("Incorrect refresh token");
        }
    }

    public Authentication validateToken(String token) throws ExecutionException, InterruptedException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        TokenPayloadDto tokenPayloadDto = gson.fromJson(decodedString, TokenPayloadDto.class);

        if (tokenPayloadDto.getTypeToken().equals("refresh")) {
            if (authenticationService.findRefreshTokenByToken(token).get() == null) throw new RuntimeException("Incorrect refresh token");
        }

        UserDto user = authenticationService.findByLogin(decoded.getSubject()).get();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public Authentication validateCredentials(CredentialsDto credentialsDto) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        UserDto user = authenticationService.authenticate(credentialsDto).get();
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }


}
