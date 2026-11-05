package com.biopix.telemedicine_backend.video.internal.twilio.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TwilioConfig {

    private final TwilioProperties twilioProperties;


    @PostConstruct
    public void init() {

        Twilio.init(
                twilioProperties.getApiKey(),
                twilioProperties.getApiSecret(),
                twilioProperties.getAccountSid()
        );
    }
}