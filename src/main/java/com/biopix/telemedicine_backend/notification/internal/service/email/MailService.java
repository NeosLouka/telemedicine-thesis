package com.biopix.telemedicine_backend.notification.internal.service.email;

import com.biopix.telemedicine_backend.notification.internal.dto.EmailMessage;

public interface MailService {

    void send(EmailMessage message);
}
