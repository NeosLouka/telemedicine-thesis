package com.biopix.telemedicine_backend.notification.internal.listener;

import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorRegistrationApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorReviewedTrigger {

    private final EmailNotificationService emailNotificationService;

    @ApplicationModuleListener
    public void on (DoctorRegistrationApprovedEvent event){

        emailNotificationService.sendDoctorApprovedEmail(event.email());
    }

}
