package com.biopix.telemedicine_backend.doctor.internal.controller;


import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentUploadResponse;
import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentUrlResponse;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.service.DocumentService;
import com.biopix.telemedicine_backend.doctor.internal.storage.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/doctor/registration/document")
public class DocumentController {

    private final S3Service s3Service;
    private final DocumentService documentService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('DOCTOR_REVIEW_FLOW_ALLOWED')")
    public ResponseEntity<DocumentUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("doc_type") DocumentType docType,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ){
            String email = authenticatedUser.getUsername();

            DocumentUploadResponse response =  s3Service.uploadFile(file,docType.name(), email);

            return ResponseEntity.ok(response);
    }

//    //{registrationId}/{documentType}/{dateTime}_{originalFileName}
    @GetMapping("/{documentType}/download-urls")
    @PreAuthorize("hasAnyAuthority('DOCTOR_REVIEW_FLOW_ALLOWED', 'FULL_ACCESS')")
    public ResponseEntity<List<DocumentUrlResponse>> getDocument(
            @PathVariable("documentType") DocumentType docType,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ){
            String email = authenticatedUser.getUsername();

            List<DocumentUrlResponse> response =  documentService.getDocumentDownloadUrl(email, docType);

            return ResponseEntity.ok().body(response);
    }
}