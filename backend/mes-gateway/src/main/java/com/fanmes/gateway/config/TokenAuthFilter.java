package com.fanmes.gateway.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TokenAuthFilter extends OncePerRequestFilter {
    private final boolean authEnabled;

    public TokenAuthFilter(@Value("${fanmes.gateway.auth-enabled:false}") boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        if (!authEnabled
                || "OPTIONS".equalsIgnoreCase(request.getMethod())
                || requestUri.contains("/health")
                || "/api/auth/login".equals(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        String accessToken = request.getHeader("X-Access-Token");
        if (StringUtils.hasText(authorization) || StringUtils.hasText(accessToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"code\":401,\"message\":\"missing token\",\"data\":null}");
    }
}
