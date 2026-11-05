package com.biopix.telemedicine_backend.notification.internal.listener;


import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileCreatedTrigger {

    private final EmailNotificationService emailNotificationService;

    @ApplicationModuleListener
    public void on(ProfileCompletedEvent event){

        emailNotificationService.sendWelcomeEmail(event.email());
    }


}
