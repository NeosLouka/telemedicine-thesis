package com.biopix.telemedicine_backend.doctor.internal.repo;

import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Registration findByDoctorId(Long doctorId);

    List<Registration> findByReviewResult(ReviewResult reviewResult);

    boolean existsByDoctorId(Long id);
}
