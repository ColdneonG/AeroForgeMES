package com.fanmes.common.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fanmes.common.api.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHENTICATED_USER_ATTRIBUTE = JwtAuthenticationFilter.class.getName() + ".user";

    private final JwtTokenService tokens;
    private final ObjectMapper mapper;
    private final boolean enabled;

    public JwtAuthenticationFilter(
            JwtTokenService tokens,
            ObjectMapper mapper,
            @Value("${fanmes.security.auth-enabled:true}") boolean enabled) {
        this.tokens = tokens;
        this.mapper = mapper;
        this.enabled = enabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !enabled
                 || "OPTIONS".equalsIgnoreCase(request.getMethod())
                 || request.getRequestURI().contains("/health")
                 || "/api/auth/login".equals(request.getRequestURI())
                 || "/api/auth/refresh".equals(request.getRequestURI())
                 || "/api/auth/logout".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            request.setAttribute(AUTHENTICATED_USER_ATTRIBUTE, tokens.verify(request.getHeader("Authorization")));
            chain.doFilter(request, response);
        } catch (RuntimeException exception) {
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            mapper.writeValue(response.getWriter(), ApiResponse.error(401, exception.getMessage()));
        }
    }
}
