package com.biopix.telemedicine_backend.notification.internal.listener;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.enums.NotificationTitle;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.biopix.telemedicine_backend.shared.contracts.events.ConsultationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultationNotificationListener {

    private final FCMRepository fcmRepository;
    private final FCMService fcmService;

    @ApplicationModuleListener
    public void on(ConsultationCreatedEvent event){

        if(event.patientAccountId() == null
                || event.doctorAccountId() == null
                || event.price()==null
                || event.scheduledStartTime()==null
        ){
            log.error("ConsultationCreatedListener failed");
            return;
        }

        Optional<UserFcmToken> doctorToken = fcmRepository.findByAccountId(event.doctorAccountId());
        Optional<UserFcmToken> patientToken = fcmRepository.findByAccountId(event.patientAccountId());
        if (doctorToken.isEmpty() || patientToken.isEmpty()) {
            log.warn("Skipping FCM: missing token for doctor={}, patient={}",
                    event.doctorAccountId(), event.patientAccountId());
            return;
        }

        UserFcmToken doctor = doctorToken.get();
        UserFcmToken patient = patientToken.get();

        String doctorBody = "Offer Accepted! You have a scheduled consultation at " + event.scheduledStartTime();
        String patientBody = "Consultation Scheduled! Your appointment is set for " + event.scheduledStartTime();
        String title = NotificationTitle.SCHEDULED_CONSULTATION.getTitle();
        String type = NotificationTitle.SCHEDULED_CONSULTATION.getDefaultType();


        boolean doctorSent = fcmService.sendMessageToUser(doctor.getFcmToken(), title, doctorBody, type);
        boolean patientSent = fcmService.sendMessageToUser(patient.getFcmToken(), title, patientBody, type);

        if (doctorSent && patientSent) {
            log.info("Successfully delivered notifications to both doctor ({}) and patient ({})",
                    event.doctorAccountId(), event.patientAccountId());
        } else {
            log.warn("Notification dispatch failed for consultation. Doctor sent: {}, Patient sent: {}", doctorSent, patientSent);

            throw new IllegalStateException(
                    String.format("Failed to deliver notification to all parties. Doctor: %s, Patient: %s", doctorSent, patientSent)
            );
        }

    }

}
