package com.biopix.telemedicine_backend.doctor.internal.repo;

import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByEmail(String email);

    Doctor findByEmail(String email);

    Optional <Doctor> findByAccountId(Long id);

    @Query("""
        SELECT d.accountId 
        FROM Doctor d 
        WHERE d.speciality.id = :specialityId 
          AND d.availabilityStatus = :status
    """)
    List<Long> findAccountIdsBySpecialityIdAndAvailabilityStatus(
            @Param("specialityId") Long specialityId,
            @Param("status") DoctorAvailabilityStatus status
    );

}
