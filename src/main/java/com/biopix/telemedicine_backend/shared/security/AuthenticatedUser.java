package com.biopix.telemedicine_backend.shared.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthenticatedUser {
    Long getUserId();

    String getUsername();

    String getRole();

    String getAccountStatus();

    Collection<? extends GrantedAuthority> getAuthorities();
}
