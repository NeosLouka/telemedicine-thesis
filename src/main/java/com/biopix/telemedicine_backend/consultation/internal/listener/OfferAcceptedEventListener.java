package com.biopix.telemedicine_backend.consultation.internal.listener;

import com.biopix.telemedicine_backend.consultation.internal.service.ConsultationService;
import com.biopix.telemedicine_backend.shared.contracts.events.OfferAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferAcceptedEventListener {

    private final ConsultationService service;

    @ApplicationModuleListener
    public void onOfferAcceptedEvent(OfferAcceptedEvent event)
    {
        service.createConsultation(event);
    }

}
