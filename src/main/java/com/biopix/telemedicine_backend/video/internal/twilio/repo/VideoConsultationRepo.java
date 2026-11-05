package com.biopix.telemedicine_backend.video.internal.twilio.repo;

import com.biopix.telemedicine_backend.video.internal.twilio.entity.VideoSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoConsultationRepo extends JpaRepository<VideoSession, Long> {
    Optional<VideoSession> findByConsultationId(Long consultationId);

    Optional<VideoSession> findByRoomName(String roomName);
}
