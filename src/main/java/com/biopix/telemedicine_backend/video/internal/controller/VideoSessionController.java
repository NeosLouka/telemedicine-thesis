package com.biopix.telemedicine_backend.video.internal.controller;


import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.video.internal.twilio.dto.JoinVideoResponse;
import com.biopix.telemedicine_backend.video.internal.twilio.dto.VideoSessionStatusResponse;
import com.biopix.telemedicine_backend.video.internal.twilio.service.VideoConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoSessionController {

    private final VideoConsultationService videoService;

    @PostMapping("/{consultationId}/join")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<JoinVideoResponse> joinConsultation(
            @PathVariable Long consultationId,
            @AuthenticationPrincipal AuthenticatedUser user
    ){

        return ResponseEntity.ok(
                videoService.joinVideoSession(
                        consultationId,
                        user.getUserId(),
                        user.getRole()
                ));

    }

    @GetMapping("/{consultationId}")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<VideoSessionStatusResponse> getConsultation(
            @PathVariable Long consultationId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                videoService.getVideoSession(consultationId, user.getUserId())
        );
    }

}
