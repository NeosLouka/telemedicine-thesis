package com.biopix.telemedicine_backend.marketplace;

import com.biopix.telemedicine_backend.marketplace.internal.dto.*;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import com.biopix.telemedicine_backend.marketplace.internal.service.ConsultationRequestService;
import com.biopix.telemedicine_backend.marketplace.internal.service.DoctorOfferService;
import com.biopix.telemedicine_backend.shared.contracts.events.OfferAcceptedEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
public class MarketPlaceFlowTest {

    private final ConsultationRequestService requestService;

    private final DoctorOfferService doctorOfferService;


    @Test
    void patientCreatesRequest_DoctorCreatesOffer_PatientAcceptsOffer(Scenario scenario) {

        //Patient creates consultation request
        Long patientId = 1L;
        String patientEmail = "patient@test.com";

        ConsultationRequestDto consultationRequestDto = new ConsultationRequestDto(
                1L,
                "I need medical help.",
                ConsultationRequestType.NOW,
                null
        );

        ConsultationRequestResponse requestResponse = requestService.create(
                patientId,
                patientEmail,
                consultationRequestDto
        );

        assertThat(requestResponse).isNotNull();
        assertThat(requestResponse.id()).isNotNull();
        assertThat(requestResponse.status()).isEqualTo(ConsultationRequestStatus.OPEN);

        // Doctor creates offer
        Long doctorId = 2L;
        String doctorEmail = "doctor@test.com";

        DoctorOfferRequest doctorOfferRequest = new DoctorOfferRequest(
                requestResponse.id(),
                BigDecimal.valueOf(100),
                null,
                30,
                "I can see you now."
        );

        DoctorOfferResponse offerResponse = doctorOfferService.create(
                doctorId,
                doctorEmail,
                doctorOfferRequest
        );

        // 3. Patient accepts offer and Scenario waits for published event
        scenario.stimulate(() -> doctorOfferService.acceptOffer(patientId, offerResponse.offerId()))
                .andWaitForEventOfType(OfferAcceptedEvent.class)
                .matching(event ->
                        event.requestId().equals(requestResponse.id()) &&
                                event.offerId().equals(offerResponse.offerId())
                )
                .toArriveAndVerify(event -> {
                    assertThat(event.requestId()).isEqualTo(requestResponse.id());
                    assertThat(event.offerId()).isEqualTo(offerResponse.offerId());

                    assertThat(event.patientAccountId()).isEqualTo(patientId);
                    assertThat(event.doctorAccountId()).isEqualTo(doctorId);

                    assertThat(event.patientEmail()).isEqualTo(patientEmail);
                    assertThat(event.doctorEmail()).isEqualTo(doctorEmail);

                    assertThat(event.price()).isEqualByComparingTo(BigDecimal.valueOf(100));
                    assertThat(event.requestType()).isEqualTo(ConsultationRequestType.NOW.name());
                    assertThat(event.scheduledStartTime()).isNotNull();
                    assertThat(event.estimatedDurationMinutes()).isEqualTo(30);
                    assertThat(event.acceptedAt()).isNotNull();
                });
    }
}
