package com.biopix.telemedicine_backend.consultation.internal.repo;

import com.biopix.telemedicine_backend.consultation.internal.entity.Consultation;
import com.biopix.telemedicine_backend.consultation.internal.enums.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findAllByStatusAndPatientAccountIdOrStatusAndDoctorAccountId(
            ConsultationStatus status1, Long patientAccountId,
            ConsultationStatus status2, Long doctorAccountId
    );
}
