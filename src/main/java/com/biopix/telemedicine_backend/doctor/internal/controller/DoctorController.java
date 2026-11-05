package com.biopix.telemedicine_backend.doctor.internal.controller;

import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.doctor.internal.dto.*;
import com.biopix.telemedicine_backend.doctor.internal.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;



    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('PROFILE_COMPLETION_ALLOWED')")
    public ResponseEntity<DoctorInsertProfileDetailsResponse> createProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody DoctorInsertProfileDetailsRequest profileDetails
    ){
        Long userId = authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();


        DoctorInsertProfileDetailsResponse response =  doctorService.saveDoctorProfile(userId, email, profileDetails);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('DOCTOR_REVIEW_FLOW_ALLOWED', 'FULL_ACCESS')")
    public ResponseEntity<DoctorBasicDetailsResponse> getProfile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id = authenticatedUser.getUserId();

        DoctorBasicDetailsResponse response = doctorService.getBasicDetails(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/details")
    @PreAuthorize("hasAnyAuthority('DOCTOR_REVIEW_FLOW_ALLOWED', 'FULL_ACCESS')")
    public ResponseEntity<DoctorFullDetailsResponse> getFullDetails(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id = authenticatedUser.getUserId();

        DoctorFullDetailsResponse response = doctorService.getFullDetails(id);
        return ResponseEntity.ok(response);

    }


    @PatchMapping("/availability")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<AvailabilityStatusResponse> changeAvailabilityStatus (
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam DoctorAvailabilityStatus status
    ){

        Long id = authenticatedUser.getUserId();

        AvailabilityStatusResponse response = doctorService.changeAvailabilityStatus(id, status);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/specialities")
    public List<SpecialityResponse> getSpecialities(){

        return doctorService.getSpecialities();
    }



}
