package com.biopix.telemedicine_backend.auth.internal.repo;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

    Optional<AuthAccount> findByEmail(String email);

    boolean existsByEmail(String email);
}
