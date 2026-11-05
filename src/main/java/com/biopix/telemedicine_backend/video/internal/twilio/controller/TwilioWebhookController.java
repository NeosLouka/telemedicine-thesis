package com.biopix.telemedicine_backend.video.internal.twilio.controller;

import com.biopix.telemedicine_backend.video.internal.twilio.dto.TwilioRoomWebhookRequest;
import com.biopix.telemedicine_backend.video.internal.twilio.service.VideoConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/twilio")
@RequiredArgsConstructor
public class TwilioWebhookController {

    private final VideoConsultationService videoService;

    @PostMapping(
            value = "/room-status",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Void> handleRoomStatusCallback(@ModelAttribute TwilioRoomWebhookRequest payload) {
        videoService.processRoomEndedWebhook(payload);
        return ResponseEntity.ok().build();
    }
}