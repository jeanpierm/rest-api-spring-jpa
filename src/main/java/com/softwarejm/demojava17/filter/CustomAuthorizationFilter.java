package com.softwarejm.demojava17.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwarejm.demojava17.util.JwtUtil;
import com.softwarejm.demojava17.util.ResponseUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.softwarejm.demojava17.config.Keys.*;
import static com.softwarejm.demojava17.config.Names.BEARER;
import static com.softwarejm.demojava17.config.Names.HEADER_ERROR;
import static com.softwarejm.demojava17.config.Paths.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals(LOGIN_URI) || request.getServletPath().equals(REFRESH_TOKEN_URI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
            try {
                String token = authorizationHeader.substring(BEARER.length());
                String username = JwtUtil.getSubject(token);
                String[] roles = JwtUtil.getClaim(token, ROLES_CLAIM_KEY).asArray(String.class);
                List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                log.error("Error logging in {}", exception.getMessage());
                ResponseUtil.responseGenericForbidden(exception, response);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }
}
