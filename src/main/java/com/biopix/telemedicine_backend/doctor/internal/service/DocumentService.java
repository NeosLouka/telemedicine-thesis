package com.biopix.telemedicine_backend.doctor.internal.service;


import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentUrlResponse;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DoctorNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DocumentFileMissingException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DocumentNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import com.biopix.telemedicine_backend.doctor.internal.storage.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class DocumentService  {

    private final DocumentRepository documentRepository;
    private final S3Service s3Service;
    private final DoctorRepository doctorRepository;
    private final RegistrationRepository registrationRepository;


    public List<DocumentUrlResponse> getDocumentDownloadUrl(String email, DocumentType docType){

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            throw new DoctorNotFoundException();
        }

        Registration registration = registrationRepository.findByDoctorId(doctor.getId());

        Document document = documentRepository.findByRegistration_IdAndDocType(registration.getId(), docType)
                .orElseThrow(() -> new DocumentNotFoundException(registration.getId(), docType));


        if (document.getFileKey() == null || document.getFileKey().isBlank()) {
            throw new DocumentFileMissingException(docType);
        }

        String presignedUrl = s3Service.generatePresignedUrl(document.getFileKey());

        DocumentUrlResponse response = new DocumentUrlResponse(
                document.getId(),
                registration.getId(),
                document.getDocType(),
                presignedUrl,
                s3Service.getExpiration()
        );

        return List.of(response);
    }

    public String generatePresignedUrlForDocument(String fileKey) {

        return s3Service.generatePresignedUrl(fileKey);
    }
}
