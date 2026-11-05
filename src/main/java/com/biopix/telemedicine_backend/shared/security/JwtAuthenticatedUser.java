package com.biopix.telemedicine_backend.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RequiredArgsConstructor
public class JwtAuthenticatedUser implements AuthenticatedUser {

    private final Long userId;
    private final String email;
    private final String role;
    private final String accountStatus;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public String getAccountStatus() {
        return accountStatus;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
