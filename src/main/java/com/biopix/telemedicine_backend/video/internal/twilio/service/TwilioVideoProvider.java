package com.biopix.telemedicine_backend.video.internal.twilio.service;

import com.biopix.telemedicine_backend.video.internal.twilio.config.TwilioProperties;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.VideoGrant;
import com.twilio.rest.video.v1.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class TwilioVideoProvider {

    private final TwilioProperties twilioProperties;

    @Value("${app.twilio.webhook-base-url:}")
    private String webhookBaseUrl;



    public String createRoom(String roomName) {

        Room room = Room.creator()
                .setUniqueName(roomName)
                .setType(Room.RoomType.GROUP)
                .setStatusCallback(URI.create(statusCallbackUrl()))
                .create();


        return room.getSid();
    }

    public String generateAccessToken(String identity, String roomName) {

        VideoGrant grant = new VideoGrant();

        grant.setRoom(roomName);

        AccessToken token =
                new AccessToken.Builder(
                        twilioProperties.getAccountSid(),
                        twilioProperties.getApiKey(),
                        twilioProperties.getApiSecret()
                )
                        .identity(identity)
                        .grant(grant)
                        .build();

        return token.toJwt();

    }

    private String statusCallbackUrl() {
        String base = webhookBaseUrl == null ? "" : webhookBaseUrl.trim();
        if (base.isBlank()) {
            throw new IllegalStateException("app.twilio.webhook-base-url is not set");
        }
        String path = "/api/webhooks/twilio/room-status";
        if (base.endsWith(path)) {
            return base;
        }
        return base.replaceAll("/+$", "") + path;
    }
}