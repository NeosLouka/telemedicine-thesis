package com.biopix.telemedicine_backend.consultation;


import com.biopix.telemedicine_backend.consultation.internal.entity.Consultation;
import com.biopix.telemedicine_backend.consultation.internal.enums.RequestType;
import com.biopix.telemedicine_backend.consultation.internal.repo.ConsultationRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ConsultationCreatedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.OfferAcceptedEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
public class OfferAcceptedEventListenerTest {

    private final ConsultationRepository consultationRepository;

    @BeforeEach
    void cleanDatabase() {
        consultationRepository.deleteAll();
    }

    @Test
    void offerAcceptedEvent_ShouldCreateConsultationAndPublishConsultationCreatedEvent(Scenario scenario) {
        Long requestId = 1L;
        Long offerId = 10L;

        Long patientAccountId = 100L;
        Long doctorAccountId = 200L;

        String patientEmail = "patient@example.com";
        String doctorEmail = "doctor@example.com";

        BigDecimal price = BigDecimal.valueOf(100);
        String requestType = "NOW";
        Instant scheduledStartTime = Instant.now().plusSeconds(60).truncatedTo(ChronoUnit.MINUTES);
        Integer estimatedDurationMinutes = 30;
        Instant acceptedAt = Instant.now().truncatedTo(ChronoUnit.MINUTES);

        OfferAcceptedEvent event = new OfferAcceptedEvent(
                requestId,
                offerId,
                patientAccountId,
                doctorAccountId,
                patientEmail,
                doctorEmail,
                price,
                requestType,
                scheduledStartTime,
                estimatedDurationMinutes,
                acceptedAt
        );

        scenario.publish(event)
                .andWaitForEventOfType(ConsultationCreatedEvent.class)
                .matching(consultationCreatedEvent ->
                        consultationCreatedEvent.patientAccountId().equals(patientAccountId)
                                && consultationCreatedEvent.doctorAccountId().equals(doctorAccountId)
                                && consultationCreatedEvent.patientEmail().equals(patientEmail)
                                && consultationCreatedEvent.doctorEmail().equals(doctorEmail)
                )
                .toArriveAndVerify(consultationCreatedEvent -> {
                    assertThat(consultationCreatedEvent.consultationId()).isNotNull();
                    assertThat(consultationCreatedEvent.patientAccountId()).isEqualTo(patientAccountId);
                    assertThat(consultationCreatedEvent.doctorAccountId()).isEqualTo(doctorAccountId);
                    assertThat(consultationCreatedEvent.patientEmail()).isEqualTo(patientEmail);
                    assertThat(consultationCreatedEvent.doctorEmail()).isEqualTo(doctorEmail);
                    assertThat(consultationCreatedEvent.price()).isEqualByComparingTo(price);
                    assertThat(consultationCreatedEvent.scheduledStartTime()).isEqualTo(scheduledStartTime);

                    Consultation savedConsultation = consultationRepository
                            .findById(consultationCreatedEvent.consultationId())
                            .orElseThrow();

                    assertThat(savedConsultation.getPatientRequestId()).isEqualTo(requestId);
                    assertThat(savedConsultation.getDoctorOfferId()).isEqualTo(offerId);
                    assertThat(savedConsultation.getPatientAccountId()).isEqualTo(patientAccountId);
                    assertThat(savedConsultation.getDoctorAccountId()).isEqualTo(doctorAccountId);
                    assertThat(savedConsultation.getPatientEmail()).isEqualTo(patientEmail);
                    assertThat(savedConsultation.getDoctorEmail()).isEqualTo(doctorEmail);
                    assertThat(savedConsultation.getPrice()).isEqualByComparingTo(price);
                    assertThat(savedConsultation.getRequestType()).isEqualTo(RequestType.NOW);
                    assertThat(savedConsultation.getScheduledStartTime()).isEqualTo(scheduledStartTime);
                    assertThat(savedConsultation.getEstimatedDurationMinutes()).isEqualTo(estimatedDurationMinutes);
                });
    }

}