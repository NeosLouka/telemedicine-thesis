package com.biopix.telemedicine_backend.notification.internal.controller;


import com.biopix.telemedicine_backend.notification.internal.dto.RegisterFcmTokenRequest;
import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/fcm-tokens")
@PreAuthorize("hasAuthority('FULL_ACCESS')")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FCMService fcmService;

    @PostMapping
    public ResponseEntity<Void> registerFcmToken(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody RegisterFcmTokenRequest request
    ){
        Long userId = authenticatedUser.getUserId();

        fcmService.registerOrUpdateToken(userId, request.fcmToken(), request.deviceType());

        return ResponseEntity.ok().build();

    }

}
