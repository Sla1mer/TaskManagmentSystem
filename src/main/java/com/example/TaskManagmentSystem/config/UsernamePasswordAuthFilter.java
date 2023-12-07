package com.example.TaskManagmentSystem.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import com.example.TaskManagmentSystem.dto.auth.CredentialsDto;
import com.example.TaskManagmentSystem.dto.exceptions.ErrorDto;
import com.example.TaskManagmentSystem.dto.exceptions.InvalidEmailOrPasswordException;
import com.example.TaskManagmentSystem.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class UsernamePasswordAuthFilter extends OncePerRequestFilter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserAuthenticationProvider userAuthenticationProvider;

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    public UsernamePasswordAuthFilter(UserAuthenticationProvider userAuthenticationProvider) {
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        if ("/api/signIn".equals(httpServletRequest.getServletPath())
                && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            CredentialsDto credentialsDto = MAPPER.readValue(httpServletRequest.getInputStream(), CredentialsDto.class);

            try {
                SecurityContextHolder.getContext().setAuthentication(
                        userAuthenticationProvider.validateCredentials(credentialsDto));
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                if (e instanceof InvalidEmailOrPasswordException) {
                    // Обработка ошибки InvalidEmailOrPasswordException
                    httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
                } else {
                    throw e;
                }
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ErrorDto errorDto = new ErrorDto(e.getMessage());
                logger.error(e.getMessage());
                MAPPER.writeValue(httpServletResponse.getOutputStream(), errorDto);
                return;
            } catch (NoSuchAlgorithmException | ExecutionException |
                    InterruptedException e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
