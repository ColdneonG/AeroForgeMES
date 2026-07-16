package com.fanmes.common.security;

import com.fanmes.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Issues and verifies HS256 access tokens using the JJWT library. */
@Component
public class JwtTokenService {

    private final SecretKey signingKey;
    private final String issuer;
    private final long ttl;
    private final long refreshTtl;

    public JwtTokenService(
            @Value("${fanmes.jwt.secret:${FANMES_JWT_SECRET:fan-mes-change-this-development-secret-2026}}") String secret,
            @Value("${fanmes.jwt.issuer:fan-mes}") String issuer,
            @Value("${fanmes.jwt.expires-in-seconds:900}") long ttl,
            @Value("${fanmes.jwt.refresh-expires-in-seconds:604800}") long refreshTtl) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.ttl = ttl;
        this.refreshTtl = refreshTtl;
    }

    public String createToken(Long uid, String username, Collection<String> roles, Collection<String> permissions) {
        try {
            Instant now = Instant.now();
            return Jwts.builder()
                    .issuer(issuer)
                    .subject(username)
                    .claim("typ", "access")
                    .claim("uid", uid)
                    .claim("roles", roles == null ? Set.of() : roles)
                    .claim("permissions", permissions == null ? Set.of() : permissions)
                    .issuedAt(java.util.Date.from(now))
                    .expiration(java.util.Date.from(now.plusSeconds(ttl)))
                    .signWith(signingKey)
                    .compact();
        } catch (RuntimeException exception) {
            throw new BusinessException("JWT 创建失败");
        }
    }

    public AuthenticatedUser verify(String authorization) {
        try {
            if (!StringUtils.hasText(authorization) || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
                throw new IllegalArgumentException("Missing Bearer token");
            }
            Claims claims = parse(authorization.substring(7).trim());
            if ("refresh".equals(claims.get("typ", String.class))) {
                throw new IllegalArgumentException("Refresh token cannot be used as an access token");
            }
            return authenticatedUser(claims);
        } catch (RuntimeException exception) {
            throw new BusinessException("无效或已过期的 token");
        }
    }

    /** Creates a long-lived token that may only be presented to the refresh endpoint. */
    public String createRefreshToken(Long uid, String username) {
        try {
            Instant now = Instant.now();
            return Jwts.builder()
                    .issuer(issuer)
                    .subject(username)
                    .claim("uid", uid)
                    .claim("typ", "refresh")
                    .id(UUID.randomUUID().toString())
                    .issuedAt(java.util.Date.from(now))
                    .expiration(java.util.Date.from(now.plusSeconds(refreshTtl)))
                    .signWith(signingKey)
                    .compact();
        } catch (RuntimeException exception) {
            throw new BusinessException("Refresh token 创建失败");
        }
    }

    public AuthenticatedUser verifyRefreshToken(String token) {
        try {
            Claims claims = parse(token);
            if (!"refresh".equals(claims.get("typ", String.class))) {
                throw new IllegalArgumentException("Not a refresh token");
            }
            return authenticatedUser(claims);
        } catch (RuntimeException exception) {
            throw new BusinessException("无效或已过期的刷新令牌");
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private AuthenticatedUser authenticatedUser(Claims claims) {
        Number uid = claims.get("uid", Number.class);
        if (uid == null || !StringUtils.hasText(claims.getSubject())) {
            throw new IllegalArgumentException("Required JWT claims are missing");
        }
        return new AuthenticatedUser(
                uid.longValue(),
                claims.getSubject(),
                toSet(claims.get("roles")),
                toSet(claims.get("permissions")));
    }

    private Set<String> toSet(Object value) {
        if (!(value instanceof Collection<?> collection)) {
            return Set.of();
        }
        Set<String> values = new LinkedHashSet<>();
        collection.forEach(item -> values.add(String.valueOf(item)));
        return Set.copyOf(values);
    }
}
