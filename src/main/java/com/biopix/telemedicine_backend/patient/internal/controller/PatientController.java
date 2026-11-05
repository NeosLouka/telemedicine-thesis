package com.biopix.telemedicine_backend.patient.internal.controller;

import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientBasicDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientFullDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientInsertProfileDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/patient")
@RestController
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('PROFILE_COMPLETION_ALLOWED')")
    public ResponseEntity<PatientInsertProfileDetailsResponse> createProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody PatientInsertProfileDetailsRequest profileDetails
    ){
        Long userId = authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();

        PatientInsertProfileDetailsResponse response =  patientService.savePatientProfile(userId, email, profileDetails);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<PatientBasicDetailsResponse> getProfile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id = authenticatedUser.getUserId();

        PatientBasicDetailsResponse response = patientService.getBasicDetails(id);
        return ResponseEntity.ok(response);

    }



    @GetMapping("/details")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<PatientFullDetailsResponse> getFullDetails(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id = authenticatedUser.getUserId();

        PatientFullDetailsResponse response = patientService.getFullDetails(id);
        return ResponseEntity.ok(response);
    }


}
