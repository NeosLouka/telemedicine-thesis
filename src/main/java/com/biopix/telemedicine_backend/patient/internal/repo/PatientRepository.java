package com.biopix.telemedicine_backend.patient.internal.repo;

import com.biopix.telemedicine_backend.patient.internal.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    boolean existsByEmail(String email);

    Patient findByAccountId(Long id);
}
