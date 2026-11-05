package com.biopix.telemedicine_backend.doctor.internal.repo;

import com.biopix.telemedicine_backend.doctor.internal.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {

    Speciality findByName(String name);
}
