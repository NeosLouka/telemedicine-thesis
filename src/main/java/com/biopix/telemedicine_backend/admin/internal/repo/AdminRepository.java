package com.biopix.telemedicine_backend.admin.internal.repo;

import com.biopix.telemedicine_backend.admin.internal.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);

    Optional<Admin> findByEmail(String email);

    Admin findByAccountId(Long id);
}
