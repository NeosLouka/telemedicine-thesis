package com.biopix.telemedicine_backend.video.internal.twilio.listener;

import com.biopix.telemedicine_backend.shared.contracts.events.ConsultationCreatedEvent;
import com.biopix.telemedicine_backend.video.internal.twilio.entity.VideoSession;
import com.biopix.telemedicine_backend.video.internal.twilio.service.VideoConsultationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultationCreatedListener {

    private final VideoConsultationService videoConsultationService;

    @ApplicationModuleListener
    public void on(ConsultationCreatedEvent event){

        VideoSession videoSession = videoConsultationService.createVideoSession( event.consultationId(), event.patientAccountId(), event.doctorAccountId(), event.scheduledStartTime());
        log.info("Video session created with id: {}", videoSession.getId());

    }
}
