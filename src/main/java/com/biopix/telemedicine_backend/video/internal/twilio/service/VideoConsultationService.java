package com.biopix.telemedicine_backend.video.internal.twilio.service;

import com.biopix.telemedicine_backend.video.internal.twilio.dto.JoinVideoResponse;
import com.biopix.telemedicine_backend.video.internal.twilio.dto.TwilioRoomWebhookRequest;
import com.biopix.telemedicine_backend.video.internal.twilio.dto.VideoSessionStatusResponse;
import com.biopix.telemedicine_backend.video.internal.twilio.entity.VideoSession;
import com.biopix.telemedicine_backend.video.internal.twilio.enums.VideoSessionStatus;
import com.biopix.telemedicine_backend.video.internal.twilio.repo.VideoConsultationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

@Service
@RequiredArgsConstructor
public class VideoConsultationService {

    private final VideoConsultationRepo videoConsultationRepo;
    private final TwilioVideoProvider twilioVideoProvider;


    @Transactional
    public VideoSession createVideoSession(
            Long consultationId,
            Long patientAccountId,
            Long doctorAccountId,
            Instant scheduledStartTime){
        String roomName = "consultation-" + consultationId;

        String roomSid = null;
        try {
            roomSid = twilioVideoProvider.createRoom(roomName);
        } catch (Exception e) {
            throw new RuntimeException( "Failed to create Twilio video room",e);
        }

        return saveVideoSessionRecord(consultationId, patientAccountId, doctorAccountId, scheduledStartTime, roomName, roomSid);
    }


    @Transactional
    public VideoSession saveVideoSessionRecord(
            Long consultationId,
            Long patientAccountId,
            Long doctorAccountId,
            Instant scheduledStartTime,
            String roomName,
            String roomSid
    ) {
        VideoSession videoSession = VideoSession.builder()
                .consultationId(consultationId)
                .patientAccountId(patientAccountId)
                .doctorAccountId(doctorAccountId)
                .scheduledStartTime(scheduledStartTime)
                .roomName(roomName)
                .externalRoomId(roomSid)
                .build();

        return videoConsultationRepo.save(videoSession);
    }


    @Transactional
    public JoinVideoResponse joinVideoSession(Long consultationId, Long accountId, String role) {

        VideoSession session = videoConsultationRepo
                    .findByConsultationId(consultationId)
                    .orElseThrow(() -> new RuntimeException("Video Session not found")
                );

        boolean allowed =
                session.getDoctorAccountId().equals(accountId)
                ||
                session.getPatientAccountId().equals(accountId);

        if (!allowed) {
            throw new RuntimeException("User is not part of this consultation");
        }

        //Check time arrival
        Instant scheduledStartTime = session.getScheduledStartTime();
        if(Instant.now().isBefore(scheduledStartTime.minus(Duration.ofMinutes(5)))){
            throw new RuntimeException("Too early to join this consultation");
        }

        if(Instant.now().isAfter(scheduledStartTime.plus(Duration.ofMinutes(15)))){
            throw new RuntimeException("Session has expired");
        }




        String identity = "account-" + accountId;

        String token = twilioVideoProvider.generateAccessToken(identity, session.getRoomName());

        if(role.equalsIgnoreCase("patient")){
            session.setPatientJoined(Boolean.TRUE);
        }else if(role.equalsIgnoreCase("doctor")){
            session.setDoctorJoined(Boolean.TRUE);
        }

        videoConsultationRepo.save(session); //Save changes

        return new JoinVideoResponse(
                session.getRoomName(),
                token
        );

    }

    @Transactional
    public void processRoomEndedWebhook(TwilioRoomWebhookRequest payload) {
        if (!"room-ended".equalsIgnoreCase(payload.StatusCallbackEvent())
                && !"completed".equalsIgnoreCase(payload.RoomStatus())) {
            return; // Ignore non-completion events (like room-created, participant-connected)
        }

        String roomName = payload.RoomName();

        videoConsultationRepo.findByRoomName(roomName).ifPresent(session -> {
            if (session.getEndedAt() == null) {
                session.setEndedAt(Instant.now());
                session.setStatus(VideoSessionStatus.ENDED);

                videoConsultationRepo.save(session);
                // Optional: Publish application event to update Consultation status to COMPLETED
            }
        });
    }

    @Transactional(readOnly = true)
    public VideoSessionStatusResponse getVideoSession(Long consultationId, Long accountId) {
        VideoSession session = videoConsultationRepo
                .findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Video Session not found"));

        boolean allowed =
                session.getDoctorAccountId().equals(accountId)
                        || session.getPatientAccountId().equals(accountId);
        if (!allowed) {
            throw new RuntimeException("User is not part of this consultation");
        }

        return new VideoSessionStatusResponse(
                session.getConsultationId(),
                session.getRoomName(),
                session.getStatus(),
                session.getEndedAt()
        );
    }
}
