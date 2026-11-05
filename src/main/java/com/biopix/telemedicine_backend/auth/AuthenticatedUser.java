package com.biopix.telemedicine_backend.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthenticatedUser {
    Long getUserId();
    String getUsername();
    Collection<? extends GrantedAuthority> getAuthorities();
}