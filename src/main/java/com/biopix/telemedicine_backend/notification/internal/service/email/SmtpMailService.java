package com.biopix.telemedicine_backend.notification.internal.service.email;

import com.biopix.telemedicine_backend.notification.internal.config.MailProperties;
import com.biopix.telemedicine_backend.notification.internal.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class SmtpMailService implements MailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    public void send(EmailMessage message) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(mailProperties.from());
            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(message.body(), true);

            mailSender.send(mimeMessage);

        }catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
