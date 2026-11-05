package com.biopix.telemedicine_backend.consultation.internal.service;

import com.biopix.telemedicine_backend.consultation.internal.dto.ConsultationResponse;
import com.biopix.telemedicine_backend.consultation.internal.dto.ConsultationSearchResult;
import com.biopix.telemedicine_backend.consultation.internal.entity.Consultation;
import com.biopix.telemedicine_backend.consultation.internal.enums.ConsultationStatus;
import com.biopix.telemedicine_backend.consultation.internal.enums.RequestType;
import com.biopix.telemedicine_backend.consultation.internal.repo.ConsultationRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ConsultationCreatedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.OfferAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository repo;
    private final ApplicationEventPublisher publisher;


    @Transactional
    public void createConsultation(OfferAcceptedEvent event) {

        Consultation consultation = Consultation.builder()
                .patientRequestId(event.requestId())
                .doctorOfferId(event.offerId())
                .patientAccountId(event.patientAccountId())
                .doctorAccountId(event.doctorAccountId())
                .patientEmail(event.patientEmail())
                .doctorEmail(event.doctorEmail())
                .price(event.price())
                .requestType(RequestType.valueOf(event.requestType()))
                .scheduledStartTime(event.scheduledStartTime())
                .estimatedDurationMinutes(event.estimatedDurationMinutes())
                .build();

        Consultation savedConsultation = repo.save(consultation);

        //Publish Consultation created event
        publisher.publishEvent(new ConsultationCreatedEvent(
                savedConsultation.getId(),
                savedConsultation.getPatientAccountId(),
                savedConsultation.getDoctorAccountId(),
                savedConsultation.getPatientEmail(),
                savedConsultation.getDoctorEmail(),
                savedConsultation.getPrice(),
                savedConsultation.getScheduledStartTime()
        ));
        
    }

    public ConsultationSearchResult getConsultationsForUser(Long accountId, ConsultationStatus status) {
        List<ConsultationResponse> list = repo
                .findAllByStatusAndPatientAccountIdOrStatusAndDoctorAccountId(status, accountId, status, accountId)
                .stream()
                .map(ConsultationResponse::fromEntity)
                .toList();

        return ConsultationSearchResult.ofList(list);
    }
}
