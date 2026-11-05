package com.biopix.telemedicine_backend.admin.internal.controller;

import com.biopix.telemedicine_backend.admin.internal.dto.AdminBasicDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminFullDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminInsertProfileDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.service.AdminService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('PROFILE_COMPLETION_ALLOWED')")
    public ResponseEntity<AdminInsertProfileDetailsResponse> createProfile(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody AdminInsertProfileDetailsRequest profileDetails
    ){
        Long userId = authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();

        AdminInsertProfileDetailsResponse response =  adminService.saveAdminProfile(userId, email, profileDetails);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<AdminBasicDetailsResponse> getProfile(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id = authenticatedUser.getUserId();

        AdminBasicDetailsResponse response = adminService.getBasicDetails(id);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/details")
    @PreAuthorize("hasAuthority('FULL_ACCESS')")
    public ResponseEntity<AdminFullDetailsResponse> getFullDetails(@AuthenticationPrincipal AuthenticatedUser authenticatedUser){

        Long id= authenticatedUser.getUserId();

        AdminFullDetailsResponse response = adminService.getFullDetails(id);
        return ResponseEntity.ok(response);
    }


}

