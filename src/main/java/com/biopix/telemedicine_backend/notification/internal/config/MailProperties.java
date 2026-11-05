package com.biopix.telemedicine_backend.notification.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
public record MailProperties(
        String from,
        Subjects subjects
) {
    public record  Subjects(
            String welcome,
            String doctorApproved,
            String otpCode
    ) {
    }
}

