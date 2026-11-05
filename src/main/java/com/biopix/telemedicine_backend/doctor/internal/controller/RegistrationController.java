package com.biopix.telemedicine_backend.doctor.internal.controller;


import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.doctor.internal.dto.RegistrationCreateResponse;
import com.biopix.telemedicine_backend.doctor.internal.dto.RegistrationDetailsResponse;
import com.biopix.telemedicine_backend.doctor.internal.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/doctor/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('DOCTOR_REVIEW_FLOW_ALLOWED')")
    public ResponseEntity<RegistrationCreateResponse> create(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        String email = authenticatedUser.getUsername();

        RegistrationCreateResponse  response = registrationService.create(email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details")
    @PreAuthorize("hasAnyAuthority('DOCTOR_REVIEW_FLOW_ALLOWED', 'FULL_ACCESS')")
    public ResponseEntity<RegistrationDetailsResponse> getDetails(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        String email = authenticatedUser.getUsername();

        RegistrationDetailsResponse response = registrationService.getDetails(email);
        return ResponseEntity.ok(response);
    }

}
