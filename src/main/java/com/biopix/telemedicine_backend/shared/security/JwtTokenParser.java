package com.biopix.telemedicine_backend.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenParser {

    private final String secret;

    public JwtTokenParser(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            java.util.Date expiration = claims.getExpiration();

            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public JwtAuthenticatedUser toAuthenticatedUser(String token) {
        Claims claims = extractAllClaims(token);

        Long userId = getUserId(claims);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        String accountStatus = claims.get("accountStatus", String.class);

        List<String> authorityNames = claims.get("authorities", List.class);

        List<SimpleGrantedAuthority> authorities = authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new JwtAuthenticatedUser(
                userId,
                email,
                role,
                accountStatus,
                authorities
        );
    }


    private Long getUserId(Claims claims) {
        Object userIdValue = claims.get("userId");

        if (userIdValue instanceof Integer integerValue) {
            return integerValue.longValue();
        }

        if (userIdValue instanceof Long longValue) {
            return longValue;
        }

        if (userIdValue instanceof String stringValue) {
            return Long.parseLong(stringValue);
        }

        throw new IllegalArgumentException("Invalid userId claim");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
