package com.biopix.telemedicine_backend.notification.internal.enums;

public enum EmailTemplate {

    WELCOME("welcome"),
    DOCTOR_APPROVED("doctor-approved"),
    OTP_CODE("otp-code");

    private final String templateName;

    EmailTemplate(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
