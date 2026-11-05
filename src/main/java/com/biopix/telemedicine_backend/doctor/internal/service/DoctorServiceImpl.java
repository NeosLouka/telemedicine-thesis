package com.biopix.telemedicine_backend.doctor.internal.service;

import com.biopix.telemedicine_backend.doctor.DoctorServiceApi;
import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentDetails;
import com.biopix.telemedicine_backend.doctor.internal.dto.RegistrationInfo;
import com.biopix.telemedicine_backend.doctor.internal.dto.DoctorFullDetailsResponse;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorServiceApi {

    private final DocumentService documentService;
    private final RegistrationRepository registrationRepository;
    private final DocumentRepository documentRepository;


    @Override
    public List<Object> getRegistrations(String result) {

        List<Registration> registrations;

        if (result == null || result.isBlank()) {
            registrations = registrationRepository.findAll();
        } else {
            try {
                ReviewResult reviewResult = ReviewResult.valueOf(result.toUpperCase());
                registrations = registrationRepository.findByReviewResult(reviewResult);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid review result: " + result);
            }
        }

        return registrations.stream()
                .map(this::convertToRegistrationInfo)
                .toList();



    }

    @Override
    public List<Object> getRegistrationDocuments(
            Long registrationId,
            String status,
            String type
    ) {
        DocumentStatus documentStatus = null;
        DocumentType documentType = null;

        if (status != null && !status.isBlank()) {
            try {
                documentStatus = DocumentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid document status: " + status);            }
        }

        if (type != null && !type.isBlank()) {
            try {
                documentType = DocumentType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid document type: " + type);            }
        }

        List<Document> documents = documentRepository.findByRegistration_Id(registrationId);

        DocumentStatus finalDocumentStatus = documentStatus;
        DocumentType finalDocumentType = documentType;

        return documents.stream()
                .filter(document -> finalDocumentStatus == null || document.getStatus().equals(finalDocumentStatus))
                .filter(document -> finalDocumentType == null || document.getDocType().equals(finalDocumentType))
                .map(this::convertToDocumentDetails)
                .toList();
    }


    private Object convertToDocumentDetails(Document document) {

        if (document == null) {
            return null;
        }

        String downloadUrl = documentService.generatePresignedUrlForDocument(document.getFileKey());

        return new DocumentDetails(
                document.getId(),
                document.getDocType(),
                downloadUrl,
                document.getStatus(),
                document.getExpiryDate(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }


    /**
     * Maps doctor with DoctorFullDetailsResponse
     * @param doctor
     * @return DoctorFullDetailsResponse
     */
    private DoctorFullDetailsResponse convertToDoctorFullDetails(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return new DoctorFullDetailsResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getDateOfBirth(),
                doctor.getPhone(),
                doctor.getGender(),
                doctor.getAddress(),
                doctor.getCountry(),
                doctor.getRole(),
                doctor.getLicenseNumber(),
                doctor.getClinicAddress(),
                doctor.getClinicPhone(),
                doctor.getSpeciality().getName(),
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }

    private Object convertToRegistrationInfo(Registration registration) {
        return new RegistrationInfo(
                registration.getId(),                           // registrationId
                convertToDoctorFullDetails(registration.getDoctor()),
                registration.getReviewedAt(),
                registration.getReviewResult(),
                registration.getReviewComment(),
                registration.getCreatedAt(),
                registration.getUpdatedAt()
        );
    }
}
