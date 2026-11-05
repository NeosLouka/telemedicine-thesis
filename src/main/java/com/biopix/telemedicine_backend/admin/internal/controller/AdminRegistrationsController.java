package com.biopix.telemedicine_backend.admin.internal.controller;

import com.biopix.telemedicine_backend.admin.internal.dto.ReviewDocumentRequest;
import com.biopix.telemedicine_backend.admin.internal.dto.ReviewRegistrationRequest;
import com.biopix.telemedicine_backend.admin.internal.service.AdminReviewService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.doctor.DoctorServiceApi;

import com.biopix.telemedicine_backend.admin.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.admin.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.admin.internal.enums.ReviewResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/admin/registrations")
@PreAuthorize("hasAuthority('FULL_ACCESS')")
@RequiredArgsConstructor
public class AdminRegistrationsController {

    private final DoctorServiceApi doctorServiceApi;
    private final AdminReviewService adminReviewService;

    @GetMapping("/all")
    public List<Object> getRegistrations(
            @RequestParam(name = "reviewResult", required = false) String reviewResult
    ){

        return doctorServiceApi.getRegistrations(reviewResult);
    }

    @GetMapping("/review-results")
    public List<String > getReviewResults(){

        return Stream.of(ReviewResult.values())
                .map(ReviewResult::name)
                .toList();
    }


    @GetMapping("/document-types")
    public List<String> getDocumentTypes(){

        return Stream.of(DocumentType.values())
                .map(DocumentType::name)
                .toList();
    }


    @GetMapping("/document-statuses")
    public List<String> getDocumentStatuses(){
        return Stream.of(DocumentStatus.values())
                .map(DocumentStatus::name)
                .toList();
    }

    @GetMapping("/{registrationId}/documents")
    public List<Object> getRegistrationDocuments(
            @PathVariable("registrationId") Long registrationId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String type
    ){


        return doctorServiceApi.getRegistrationDocuments(registrationId, status , type);
    }


    /**
     * Update registration's review details
     * @param registrationId
     * @param request
     * @param authenticatedUser
     * @return
     */
    @PutMapping("/{registrationId}/review")
    public ResponseEntity<Void> reviewRegistration(
            @PathVariable("registrationId") Long registrationId,
            @RequestBody ReviewRegistrationRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        String adminEmail = authenticatedUser.getUsername();

        adminReviewService.reviewRegistration(registrationId, adminEmail, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/documents/{documentId}/review")
    public ResponseEntity<Void> reviewDocument(
            @PathVariable("documentId") Long documentId,
            @RequestBody ReviewDocumentRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser

    ) {
        String adminEmail = authenticatedUser.getUsername();


        adminReviewService.reviewDocument(documentId, adminEmail, request);

        return ResponseEntity.ok().build();
    }







}
