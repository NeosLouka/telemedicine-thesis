package com.biopix.telemedicine_backend.consultation.internal.controller;


import com.biopix.telemedicine_backend.consultation.internal.dto.ConsultationResponse;
import com.biopix.telemedicine_backend.consultation.internal.dto.ConsultationSearchResult;
import com.biopix.telemedicine_backend.consultation.internal.enums.ConsultationStatus;
import com.biopix.telemedicine_backend.consultation.internal.service.ConsultationService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<ConsultationSearchResult> getMyConsultations(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam ConsultationStatus status
    ) {
        ConsultationSearchResult response = consultationService.getConsultationsForUser(
                authenticatedUser.getUserId(),
                status
        );

        return ResponseEntity.ok(response);
    }
}
