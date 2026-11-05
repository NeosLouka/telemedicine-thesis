package com.biopix.telemedicine_backend.auth.internal.security;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuthAccountDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String passwordHash;
    private final Collection<? extends GrantedAuthority> authorities;


    public AuthAccountDetails(AuthAccount account) {
        this.userId = account.getId();
        this.email = account.getEmail();
        this.passwordHash = account.getPasswordHash();
        this.authorities = buildAuthorities(account);
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(AuthAccount account) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));

        authorities.add(new SimpleGrantedAuthority("ACCOUNT_" + account.getAccountStatus().name()));

        switch (account.getAccountStatus()) {
            case PENDING_PROFILE_COMPLETION -> authorities.add(new SimpleGrantedAuthority(AuthAuthority.PROFILE_COMPLETION_ALLOWED.name()));

            case PENDING_ADMIN_REVIEW-> authorities.add(new SimpleGrantedAuthority(AuthAuthority.DOCTOR_REVIEW_FLOW_ALLOWED.name()));

            case ACTIVE -> authorities.add(new SimpleGrantedAuthority(AuthAuthority.FULL_ACCESS.name()));

            case REJECTED -> {
                //Future implementation
            }

            case SUSPENDED -> {
                // Future implementation
            }
        }

        return authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }
    @Override
    public String getUsername() {
        return email;
    }


}
