package com.example.CRMAuthBackend.config;

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
import com.example.CRMAuthBackend.dto.auth.*;
import com.example.CRMAuthBackend.dto.entities.RefreshTokenDto;
import com.example.CRMAuthBackend.dto.entities.UserDto;
import com.example.CRMAuthBackend.dto.exceptions.*;
import com.example.CRMAuthBackend.services.AuthenticationService;
import com.example.CRMAuthBackend.services.MailSenderService;
import com.example.CRMAuthBackend.utils.RandomCodeGenerator;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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

    @Autowired
    private MailSenderService mailSenderService;

    @PostConstruct
    protected void init() {
        // this is to avoid having the raw secret key available in the JVM
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createTemporaryToken(String email, UserDto userDto) throws MessagingException {
        Date now = new Date();
        Date validityTemporary = new Date(now.getTime() + 300000); // 5 minutes (temporary token)


        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String temporaryToken = JWT.create()
                .withIssuer("CRM")
                .withSubject(email)
                .withIssuedAt(now)
                .withExpiresAt(validityTemporary)
                .withPayload(gson.toJson(new RegistrationDto(userDto, new TokenPayloadDto("2FA"))))
                .sign(algorithm);


        String secretCode = RandomCodeGenerator.generateRandomCode(6);
        Codes2FADto codes2FADto = new Codes2FADto(email, secretCode);

        mailSenderService.send(email, "Activation code",
                "Код активации: " + secretCode);

        authenticationService.saveCode2FA(codes2FADto);

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();

        Runnable task = () -> {
            authenticationService.delete2FACode(codes2FADto);
            scheduler.destroy();
        };

        scheduler.schedule(task, new Date(System.currentTimeMillis() + 300000)); // выполнить через 5 минут

        return temporaryToken;
    }

    public TokensDto validateTemporaryToken(HttpServletRequest request, String code) throws ExecutionException, InterruptedException, Incorrect2FATokenException, Incorrect2FACodeException, NoSuchAlgorithmException, EmailExistsException {
        String token;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
        } else throw new RuntimeException("Incorrect temporary token");


        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        RegistrationDto registrationDto = gson.fromJson(decodedString, RegistrationDto.class);

        TokenPayloadDto tokenPayloadDto = registrationDto.getTokenPayloadDto();

        if (!tokenPayloadDto.getTypeToken().equals("2FA")) {
            throw new Incorrect2FATokenException("Не правильный тип токена");
        }

        if (!authenticationService.validate2FACode(decoded.getSubject(), code).get()) {
            throw new Incorrect2FACodeException("Не правильный код или истекло время действия");
        }

        if (registrationDto.getUserDto() != null &&
                authenticationService.findByEmail(registrationDto.getUserDto().getEmail()) == null) {
            authenticationService.registrationUser(registrationDto.getUserDto());
        } else {
            throw new EmailExistsException("Такой email уже существует");
        }

        UserDto user = authenticationService.findByEmail(decoded.getSubject()).get();

        return createConstantToken(user.getEmail(), user);
    }

    public TokensDto createConstantToken(String email, UserDto userDto) throws ExecutionException, InterruptedException {
        Date now = new Date();
        Date validityAccess = new Date(now.getTime() + 1800000); // 30 minutes (access token)
        Date validityRefresh = new Date(now.getTime() + 604800000); // 7 days (refresh token)


        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String accessToken = JWT.create()
                .withIssuer("CRM")
                .withSubject(email)
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


        RefreshTokenDto refreshTokenDto = authenticationService.findRefreshTokenByUserId(userDto.getId()).get();

        if (authenticationService.findRefreshTokenByUserId(userDto.getId()).get() == null) {
            refreshTokenDto = new RefreshTokenDto();
            refreshTokenDto.setUser(authenticationService.findByEmail(email).get());
        }

        refreshTokenDto.setToken(refreshToken);
        authenticationService.saveRefreshToken(refreshTokenDto);

        return new TokensDto(accessToken, refreshToken, null, userDto.is2FA());
    }

    public TokensDto createToken(String email, UserDto regUser) throws ExecutionException, InterruptedException, MessagingException {
        if (regUser != null) {
            return new TokensDto(null, null, createTemporaryToken(email, regUser),
                    false);
        }

        UserDto userDto = authenticationService.findByEmail(email).get();

        if (userDto.is2FA()) {
            return new TokensDto(null, null, createTemporaryToken(email, userDto),
                    userDto.is2FA());
        }

        return createConstantToken(email, userDto);
    }

    public TokensDto createNewTokensByRefreshToken(HttpServletRequest request) throws ExecutionException, InterruptedException, MessagingException, IncorrectTokenException {

        String jwtToken;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        } else throw new IncorrectTokenException("Incorrect refresh token");

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(jwtToken);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        TokenPayloadDto tokenPayloadDto = gson.fromJson(decodedString, TokenPayloadDto.class);

        RefreshTokenDto refreshTokenDto = authenticationService.findRefreshTokenByToken(jwtToken).get();

        if (tokenPayloadDto.getTypeToken().equals("refresh")) {
            if (refreshTokenDto == null) throw new IncorrectTokenException("Incorrect refresh token");

            return createToken(refreshTokenDto.getUser().getNumberPhone(), null);
        } else  {
            throw new IncorrectTokenException("Incorrect refresh token");
        }
    }

    public Authentication validateToken(String token) throws ExecutionException, InterruptedException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        byte[] decodedBytes = Base64.getDecoder().decode(decoded.getPayload());
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        RegistrationDto registrationDto = gson.fromJson(decodedString, RegistrationDto.class);
        TokenPayloadDto tokenPayloadDto = registrationDto.getTokenPayloadDto();

        if (tokenPayloadDto.getTypeToken().equals("refresh")) {
            if (authenticationService.findRefreshTokenByToken(token).get() == null) throw new RuntimeException("Incorrect refresh token");
        }

        UserDto user = authenticationService.findByEmail(decoded.getSubject()).get();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public Authentication validateCredentials(CredentialsDto credentialsDto) throws NoSuchAlgorithmException, ExecutionException, InterruptedException, NotActiveAccountException, InvalidEmailOrPasswordException {
        UserDto user = authenticationService.authenticate(credentialsDto).get();
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }


}
