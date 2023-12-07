package com.example.TaskManagmentSystem.config;

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
import com.example.TaskManagmentSystem.dto.auth.*;
import com.example.TaskManagmentSystem.dto.entities.RefreshToken;
import com.example.TaskManagmentSystem.dto.entities.User;
import com.example.TaskManagmentSystem.dto.exceptions.*;
import com.example.TaskManagmentSystem.services.AuthenticationService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Gson gson;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokensDto createConstantToken(String email, User user) throws ExecutionException, InterruptedException {
        Date now = new Date();
        Date validityAccess = new Date(now.getTime() + 1800000); // 30 minutes (access token)
        Date validityRefresh = new Date(now.getTime() + 604800000); // 7 days (refresh token)


        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String accessToken = JWT.create()
                .withIssuer("CRM")
                .withSubject(String.valueOf(user.getId()))
                .withIssuedAt(now)
                .withExpiresAt(validityAccess)
                .withPayload(gson.toJson(new TokenPayloadDto("access")))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuer("CRM")
                .withSubject(email)
                .withIssuedAt(now)
                .withPayload(gson.toJson(new TokenPayloadDto("refresh")))
                .withExpiresAt(validityRefresh)
                .sign(algorithm);


        RefreshToken refreshTokenDto = authenticationService.findRefreshTokenByUserId(user.getId()).get();

        if (authenticationService.findRefreshTokenByUserId(user.getId()).get() == null) {
            refreshTokenDto = new RefreshToken();
            refreshTokenDto.setUser(authenticationService.findByEmail(email).get());
        }

        refreshTokenDto.setToken(refreshToken);
        authenticationService.saveRefreshToken(refreshTokenDto);

        return new TokensDto(accessToken, refreshToken);

    }

    public TokensDto registration(User user) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        authenticationService.registrationUser(user);

        return createConstantToken(user.getEmail(), user);
    }

    public TokensDto createToken(String email) throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        User user = authenticationService.findByEmail(email).get();

        return createConstantToken(email, user);
    }

    public String getTokenFromHeader(HttpServletRequest request) throws IncorrectTokenException {
        String jwtToken;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        } else throw new IncorrectTokenException("Incorrect refresh token");

        return jwtToken;
    }

    public TokensDto createNewTokensByRefreshToken(HttpServletRequest request) throws ExecutionException, InterruptedException, MessagingException, IncorrectTokenException, NoSuchAlgorithmException {

        String jwtToken = getTokenFromHeader(request);

        DecodedJWT decoded = verifyToken(jwtToken);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        TokenPayloadDto tokenPayloadDto = gson.fromJson(decodedString, TokenPayloadDto.class);

        RefreshToken refreshToken = authenticationService.findRefreshTokenByToken(jwtToken).get();

        if (tokenPayloadDto.getTypeToken().equals("refresh")) {
            if (refreshToken == null) throw new IncorrectTokenException("Incorrect refresh token");

            return createToken(refreshToken.getUser().getEmail());
        } else  {
            throw new IncorrectTokenException("Incorrect refresh token");
        }
    }

    public DecodedJWT verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        return verifier.verify(token);
    }

    public Authentication validateToken(String token) throws ExecutionException, InterruptedException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        String payload = decoded.getPayload();
        String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(decodedPayload).getAsJsonObject();

        String typeToken = jsonObject.get("typeToken").getAsString();

        if (typeToken.equals("refresh")) {
            if (authenticationService.findRefreshTokenByToken(token).get() == null) throw new RuntimeException("Incorrect refresh token");
        }

        User user = authenticationService.findByEmail(decoded.getSubject()).get();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

    }

    public Authentication validateCredentials(CredentialsDto credentialsDto) throws NoSuchAlgorithmException, ExecutionException, InterruptedException, InvalidEmailOrPasswordException {
        User user = authenticationService.authenticate(credentialsDto).get();
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }


}
