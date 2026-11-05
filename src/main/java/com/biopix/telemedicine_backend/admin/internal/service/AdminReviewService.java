package com.biopix.telemedicine_backend.admin.internal.service;


import com.biopix.telemedicine_backend.admin.internal.dto.ReviewDocumentRequest;
import com.biopix.telemedicine_backend.admin.internal.dto.ReviewRegistrationRequest;
import com.biopix.telemedicine_backend.admin.internal.entity.Admin;
import com.biopix.telemedicine_backend.admin.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.admin.internal.exceptions.AdminNotFoundException;
import com.biopix.telemedicine_backend.admin.internal.exceptions.InvalidRegistrationReviewException;
import com.biopix.telemedicine_backend.admin.internal.repo.AdminRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.DocumentReviewSubmittedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.RegistrationReviewSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class AdminReviewService {

     private final ApplicationEventPublisher eventPublisher;
     private final AdminRepository adminRepository;

    /**
     * Publish event for negative registration review outcome
     * such as  INFO_REQUESTED or  REJECTED_BY_ADMIN
     * @param registrationId
     * @param adminEmail
     * @param request
     */
     @Transactional
     public void reviewRegistration(Long registrationId, String adminEmail, ReviewRegistrationRequest request){

         if (request.result() == ReviewResult.APPROVED_BY_ADMIN) {
             throw new InvalidRegistrationReviewException(
                     "Registration approval is automatic when all required documents are approved"
             );
         }

         Admin admin = adminRepository.findByEmail(adminEmail)
                         .orElseThrow(AdminNotFoundException::new);

         eventPublisher.publishEvent(new RegistrationReviewSubmittedEvent(
                 registrationId,
                 admin.getId(),
                 request.result().name(),
                 request.comment(),
                 Instant.now()
         ));
     }

    /**
     * Publish for the document review outcome
     * @param documentId
     * @param request
     */
    @Transactional
    public void reviewDocument(Long documentId, String adminEmail, ReviewDocumentRequest request) {

        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(AdminNotFoundException::new);

        if (admin == null) {
            throw new AdminNotFoundException();
        }

        eventPublisher.publishEvent(new DocumentReviewSubmittedEvent(
                documentId,
                admin.getId(),
                request.status().name(),
                request.expiryDate(),
                Instant.now()
        ));
    }

}
