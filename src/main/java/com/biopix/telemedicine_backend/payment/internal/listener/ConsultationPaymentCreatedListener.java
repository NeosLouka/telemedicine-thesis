package com.biopix.telemedicine_backend.payment.internal.listener;


import com.biopix.telemedicine_backend.payment.internal.service.MockPaymentService;
import com.biopix.telemedicine_backend.shared.contracts.events.ConsultationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ConsultationPaymentCreatedListener {

    private final MockPaymentService mockPaymentService;

    @ApplicationModuleListener
    public void on(ConsultationCreatedEvent event) {
        mockPaymentService.createPaymentForConsultation(
                event.consultationId(),
                event.patientAccountId(),
                event.doctorAccountId(),
                event.price()
        );
    }

}
