package com.biopix.telemedicine_backend.notification;


public interface EmailNotificationService {

    void sendWelcomeEmail(String to);

    void sendDoctorApprovedEmail(String to);

    void sendOtpCodeEmail(String to , String otpCode);
}
