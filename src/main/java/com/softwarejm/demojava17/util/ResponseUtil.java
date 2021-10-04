package com.softwarejm.demojava17.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwarejm.demojava17.model.User;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static com.softwarejm.demojava17.config.Keys.MESSAGE_KEY;
import static com.softwarejm.demojava17.config.Names.HEADER_ERROR;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ResponseUtil {

    public static void responseGenericForbidden(Exception exception, HttpServletResponse response) throws IOException {
        response.setHeader(HEADER_ERROR, exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> payload = Map.of(MESSAGE_KEY, exception.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), payload);
    }

    public static void responseInvalidToken(Exception exception, HttpServletResponse response) throws IOException {
        response.setHeader(HEADER_ERROR, exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> payload = Map.of(MESSAGE_KEY, "Invalid session. Please login again.");
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), payload);
    }

    public static void responseBadCredentials(HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setHeader(HEADER_ERROR, failed.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> payload = Map.of(MESSAGE_KEY, failed.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), payload);
    }

    public static void responseTokens(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), JwtUtil.mapTokens(accessToken, refreshToken));
    }

    public static void responseTokensWithUserInfo(HttpServletResponse response, String accessToken, String refreshToken,
                                                  User user) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        Map<String, String> payload = new java.util.HashMap<>(JwtUtil.mapTokens(accessToken, refreshToken));
        payload.put("uid", user.getId().toString());
        payload.put("username", user.getUsername());
        payload.put("name", user.getName());
        new ObjectMapper().writeValue(response.getOutputStream(), payload);
    }
}
