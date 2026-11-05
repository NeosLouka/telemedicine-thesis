package com.biopix.telemedicine_backend.video.internal.twilio.dto;

public record TwilioRoomWebhookRequest(
        String RoomName,
        String RoomSid,
        String RoomStatus, // e.g., "completed"
        String StatusCallbackEvent, // e.g., "room-ended"
        String AccountSid
) {
}
