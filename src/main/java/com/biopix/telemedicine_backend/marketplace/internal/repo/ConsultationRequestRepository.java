package com.biopix.telemedicine_backend.marketplace.internal.repo;

import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationRequest;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Long>{
    Optional<ConsultationRequest> findByPatientAccountIdAndStatus(Long id, ConsultationRequestStatus status);

    boolean existsByPatientAccountIdAndStatus(Long id, ConsultationRequestStatus consultationRequestStatus);

    List<ConsultationRequest> findAllByPatientAccountId(Long id);

    //All Open Requests by Speciality
    List<ConsultationRequest> findAllByStatusAndSpecialityId(
            ConsultationRequestStatus consultationRequestStatus,
            Long specialityId
    );

}
