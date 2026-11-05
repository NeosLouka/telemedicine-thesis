package com.biopix.telemedicine_backend.marketplace.internal.repo;

import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationOffer;
import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationOfferRepository extends JpaRepository<ConsultationOffer,Long> {

    boolean existsByRequestIdAndDoctorAccountId(Long aLong, Long id);


    List<ConsultationOffer> findByDoctorEmailAndStatus(String doctorEmail, DoctorOfferStatus status);
    List<ConsultationOffer> findByDoctorEmail(String doctorEmail);



    Optional<ConsultationOffer> findByRequestIdAndDoctorAccountIdAndStatus(Long aLong, Long id, DoctorOfferStatus doctorOfferStatus);

    List<ConsultationOffer> findByRequestIdAndStatus(Long requestId, DoctorOfferStatus status);

    List<ConsultationOffer> findByRequestId(Long requestId);
}
