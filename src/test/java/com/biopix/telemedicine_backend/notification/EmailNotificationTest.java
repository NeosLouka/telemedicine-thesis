package com.biopix.telemedicine_backend.notification;

import com.biopix.telemedicine_backend.shared.contracts.events.DoctorRegistrationApprovedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.verify;

@ApplicationModuleTest
@WithPostgresContainer
class EmailNotificationTest {

    @MockitoBean
    private EmailNotificationService emailNotificationService;

    @Test
    void shouldSendWelcomeEmailWhenProfileCompleted(Scenario scenario) {

        String email = "user@example.com";

        ProfileCompletedEvent event =
                new ProfileCompletedEvent(
                        1L,
                        email,
                        "PATIENT"
                );

        scenario.publish(event)
                .andWaitForStateChange(() -> {
                    try {
                        verify(emailNotificationService)
                                .sendWelcomeEmail(email);

                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                });
    }

    @Test
    void shouldSendApprovedEmailWhenRegistrationApproved(Scenario scenario) {

        String email = "user@example.com";

        DoctorRegistrationApprovedEvent event =
                new DoctorRegistrationApprovedEvent(
                        1L,
                        1L,
                        1L,
                        email

                );

        scenario.publish(event)
                .andWaitForStateChange(() -> {
                    try {
                        verify(emailNotificationService)
                                .sendDoctorApprovedEmail(email);
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    }
                });
    }
}