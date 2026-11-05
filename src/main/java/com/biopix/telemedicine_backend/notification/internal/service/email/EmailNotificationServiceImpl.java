package com.biopix.telemedicine_backend.notification.internal.service.email;

import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import com.biopix.telemedicine_backend.notification.internal.config.MailProperties;
import com.biopix.telemedicine_backend.notification.internal.dto.EmailMessage;
import com.biopix.telemedicine_backend.notification.internal.enums.EmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final MailService mailService;
    private final EmailTemplateService emailTemplateService;
    private final MailProperties mailProperties;

    @Override
    public void sendWelcomeEmail(String to) {
        String html = emailTemplateService.render(EmailTemplate.WELCOME.getTemplateName());

        EmailMessage message = new EmailMessage(
                to,
                mailProperties.subjects().welcome(),
                html
        );

        mailService.send(message);

    }

    @Override
    public void sendDoctorApprovedEmail(String to) {

        String html = emailTemplateService.render(EmailTemplate.DOCTOR_APPROVED.getTemplateName());

        EmailMessage message = new EmailMessage(
                to,
                mailProperties.subjects().doctorApproved(),
                html
        );

        mailService.send(message);

    }

    @Override
    public void sendOtpCodeEmail(String to , String otpCode) {

        Map<String, Object> model = new HashMap<>();
        model.put("otpCode", otpCode);



        String html = emailTemplateService.render(EmailTemplate.OTP_CODE.getTemplateName(), model);

        EmailMessage message = new EmailMessage(
                to,
                mailProperties.subjects().otpCode(),
                html
        );

        mailService.send(message);

    }
}
