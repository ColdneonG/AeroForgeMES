package com.fanmes.common.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fanmes.common.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenService {

    private final ObjectMapper mapper;
    private final String secret;
    private final String issuer;
    private final long ttl;

    public JwtTokenService(ObjectMapper mapper,
                           @Value("${fanmes.jwt.secret:${FANMES_JWT_SECRET:fan-mes-change-this-development-secret-2026}}") String secret,
                           @Value("${fanmes.jwt.issuer:fan-mes}") String issuer,
                           @Value("${fanmes.jwt.expires-in-seconds:28800}") long ttl) {
        this.mapper = mapper;
        this.secret = secret;
        this.issuer = issuer;
        this.ttl = ttl;
    }

    public String createToken(Long uid, String username, Collection<String> roles, Collection<String> permissions) {
        try {
            long now = Instant.now().getEpochSecond();
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("iss", issuer);
            claims.put("sub", username);
            claims.put("uid", uid);
            claims.put("roles", roles == null ? Set.of() : roles);
            claims.put("permissions", permissions == null ? Set.of() : permissions);
            claims.put("iat", now);
            claims.put("exp", now + ttl);
            String header = encode(Map.of("alg", "HS256", "typ", "JWT"));
            String body = encode(claims);
            return header + "." + body + "." + sign(header + "." + body);
        } catch (Exception exception) {
            throw new BusinessException("JWT 鍒涘缓澶辫触");
        }
    }

    public AuthenticatedUser verify(String authorization) {
        try {
            if (!StringUtils.hasText(authorization) || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
                throw new IllegalArgumentException();
            }
            String[] parts = authorization.substring(7).trim().split("\\.");
            if (parts.length != 3 || !MessageDigest.isEqual(
                    sign(parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII),
                    parts[2].getBytes(StandardCharsets.US_ASCII))) {
                throw new IllegalArgumentException();
            }
            Map<String, Object> claims = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]), new TypeReference<>() {});
            if (!issuer.equals(claims.get("iss"))
                    || ((Number) claims.get("exp")).longValue() <= Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException();
            }
            return new AuthenticatedUser(
                    ((Number) claims.get("uid")).longValue(),
                    String.valueOf(claims.get("sub")),
                    toSet(claims.get("roles")),
                    toSet(claims.get("permissions")));
        } catch (Exception exception) {
            throw new BusinessException("鏃犳晥鎴栧凡杩囨湡鐨?token");
        }
    }

    private String encode(Object value) throws Exception {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(value));
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.US_ASCII)));
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
