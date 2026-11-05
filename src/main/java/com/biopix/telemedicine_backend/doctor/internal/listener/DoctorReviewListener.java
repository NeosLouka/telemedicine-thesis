package com.biopix.telemedicine_backend.doctor.internal.listener;

import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.RegistrationNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorRegistrationApprovedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.DocumentReviewSubmittedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.RegistrationReviewSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorReviewListener {

    private final RegistrationRepository registrationRepository;
    private final DocumentRepository documentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    /**
     * Handles manual registration review outcomes.
     * This should be used only for:
     * - INFO_REQUESTED
     * - REJECTED_BY_ADMIN
     * APPROVED_BY_ADMIN is handled automatically when all required documents are approved.
     */
    @ApplicationModuleListener
    public void onRegistrationReviewed(RegistrationReviewSubmittedEvent event) {

        Registration registration = registrationRepository.findById(event.registrationId())
                .orElseThrow(RegistrationNotFoundException::new);

        ReviewResult newReviewResult = ReviewResult.valueOf(event.reviewResult());

        //Also checked before publication
        if (newReviewResult == ReviewResult.APPROVED_BY_ADMIN) {
            throw new IllegalStateException("Manual registration approval is not allowed. Approval is automatic when all required documents are approved.");
        }

        //Idempotency check for event consumption
        boolean alreadyApplied =
                registration.getReviewResult() == newReviewResult
                && event.adminId().equals(registration.getReviewedByAdminId())
                && event.reviewComment().equals(registration.getReviewComment());

        if (alreadyApplied) {
            return;
        }

        registration.setReviewedByAdminId(event.adminId());
        registration.setReviewResult(ReviewResult.valueOf(event.reviewResult()));
        registration.setReviewComment(event.reviewComment());
        registration.setReviewedAt(event.reviewedAt());

        registrationRepository.save(registration);
    }


    /**
     * Handles document review.
     * After each document review, the doctor module checks whether all required
     * document types are approved. If yes, the registration is automatically approved.
     */
    @ApplicationModuleListener
    public void onDocumentReviewed(DocumentReviewSubmittedEvent event) {

        Document document =  documentRepository.findById(event.documentId())
                .orElseThrow(() -> new IllegalArgumentException("No document found with id: " + event.documentId()));


        DocumentStatus reviewStatus = DocumentStatus.valueOf(event.reviewStatus());

        document.setStatus(DocumentStatus.valueOf(event.reviewStatus()));
        document.setExpiryDate(event.expiryDate());

        documentRepository.save(document);

        Long registrationId = document.getRegistration().getId();

        if (reviewStatus == DocumentStatus.APPROVED) {
            checkAndApproveRegistrationIfReady(
                    registrationId,
                    event.adminId(),
                    event.reviewedAt()
            );
        }

    }


    private void checkAndApproveRegistrationIfReady(
            Long registrationId,
            Long adminId,
            Instant reviewedAt
    ) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("No registration found with id: " + registrationId));

        if (registration.getReviewResult() == ReviewResult.APPROVED_BY_ADMIN) {
            return;
        }

        boolean allDocumentsApproved = areAllRequiredDocumentsApproved(registrationId);

        if (!allDocumentsApproved) {
            return;
        }

        registration.setReviewResult(ReviewResult.APPROVED_BY_ADMIN);
        registration.setReviewedByAdminId(adminId);
        registration.setReviewedAt(reviewedAt);
        registration.setReviewComment("All required documents approved.");

        registrationRepository.save(registration);

        publishDoctorRegistrationApprovedEvent(registration);
    }

    private boolean areAllRequiredDocumentsApproved(Long registrationId) {

        List<Document> documents = documentRepository.findByRegistration_Id(registrationId);

        Set<DocumentType> approvedTypes = documents.stream()
                .filter(document -> document.getStatus() == DocumentStatus.APPROVED)
                .map(Document::getDocType)
                .collect(Collectors.toSet());

        Set<DocumentType> requiredTypes = Set.of(
                DocumentType.MEDICAL_LICENSE,
                DocumentType.MEDICAL_DEGREE,
                DocumentType.SPECIALITY_TITLE,
                DocumentType.MEMBERSHIP_CERT
        );

        return approvedTypes.containsAll(requiredTypes);
    }


    private void publishDoctorRegistrationApprovedEvent(Registration registration) {
        applicationEventPublisher.publishEvent(new DoctorRegistrationApprovedEvent(
                registration.getDoctor().getAccountId(),
                registration.getDoctor().getId(),
                registration.getId(),
                registration.getDoctor().getEmail()
        ));
    }
}
