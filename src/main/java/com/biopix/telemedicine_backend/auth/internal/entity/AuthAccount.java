package com.biopix.telemedicine_backend.auth.internal.entity;

import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "auth_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",
            nullable = false,
            length = 30,
            columnDefinition = "VARCHAR(30) CHECK (role IN ('PATIENT', 'DOCTOR', 'ADMIN'))"
    )
    private AuthRole role;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status",
            nullable = false,
            length = 40,
            columnDefinition = "VARCHAR(40) DEFAULT 'PENDING_PROFILE_COMPLETION' CHECK (account_status IN ('PENDING_PROFILE_COMPLETION', 'PENDING_ADMIN_REVIEW', 'ACTIVE', 'REJECTED', 'SUSPENDED'))"
    )
    private AccountStatus accountStatus = AccountStatus.PENDING_PROFILE_COMPLETION;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}