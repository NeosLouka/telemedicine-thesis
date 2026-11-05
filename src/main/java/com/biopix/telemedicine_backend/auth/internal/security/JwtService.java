package com.biopix.telemedicine_backend.auth.internal.security;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthAuthority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }


    public String generateToken(AuthAccount account) { // Use email as username
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", account.getId());
        claims.put("role", account.getRole().name());
        claims.put("accountStatus", account.getAccountStatus().name());
        claims.put("authorities", buildAuthorityNames(account));

        return createToken(claims, account.getEmail());
    }

    private List<String> buildAuthorityNames(AuthAccount account) {
        List<String> authorities = new ArrayList<>();

        authorities.add("ROLE_" + account.getRole().name());
        authorities.add("ACCOUNT_" + account.getAccountStatus().name());

        switch (account.getAccountStatus()) {
            case PENDING_PROFILE_COMPLETION ->
                    authorities.add(AuthAuthority.PROFILE_COMPLETION_ALLOWED.name());

            case PENDING_ADMIN_REVIEW ->
                    authorities.add(AuthAuthority.DOCTOR_REVIEW_FLOW_ALLOWED.name());

            case ACTIVE ->
                    authorities.add(AuthAuthority.FULL_ACCESS.name());

            case REJECTED ->
                    authorities.add("ACCOUNT_REJECTED");

            case SUSPENDED ->
                    authorities.add("ACCOUNT_SUSPENDED");
        }

        return authorities;
    }

    private String createToken(Map<String, Object> claims, String email) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }



}