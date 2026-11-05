package com.biopix.telemedicine_backend.doctor.internal.storage;

import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentUploadResponse;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.*;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;


@RequiredArgsConstructor
@Service
public class S3Service {


    private final DoctorRepository doctorRepository;
    private final RegistrationRepository registrationRepository;
    private final S3Client s3Client;
    private final DocumentRepository documentRepository;
    private final S3Presigner s3Presigner;
    private final Long expirationTime = 30L;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    /**
     * Uploads file in S3 bucket with format : {registrationId}/{documentType}/{dateTime}_{originalFileName}
     * @param file
     * @param docType
     * @param email
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Exception.class)
    public DocumentUploadResponse uploadFile(MultipartFile file, String docType, String email) {

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            throw new DoctorNotFoundException();
        }

        Registration registration = registrationRepository.findByDoctorId(doctor.getId());    //Retrieves doctor's registration

        if (registration == null) {
            throw new RegistrationNotFoundException();
        }

        DocumentType documentType = DocumentType.valueOf(docType);

        if (registration.getReviewResult() == ReviewResult.APPROVED_BY_ADMIN) {
            throw new RegistrationAlreadyApprovedException();
        }

        //Document build for insertion
        Document document = documentRepository
                .findByRegistration_IdAndDocType(registration.getId(), documentType)
                .orElseThrow(() -> new DocumentNotFoundException("Required document not found for the type: " + documentType));


        if (document.getStatus() == DocumentStatus.APPROVED) {

            if (document.getExpiryDate() == null || document.getExpiryDate().isAfter(LocalDate.now())) {
                throw new DocumentAlreadyApprovedException("Cannot re-upload an active, approved document.");
            }
        }

        Instant dateTime = Instant.now();

        String keyName = String.format("%s/%s/%s_%s",
                registration.getId(),
                docType,
                dateTime,
                file.getOriginalFilename()
        );


        //Prepare ObjectRequest
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();


        boolean uploadedToS3  = false;

        try {

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if (!putObjectResponse.sdkHttpResponse().isSuccessful() || putObjectResponse.eTag() == null) {
                throw new IOException("Upload Failed Response status: " + putObjectResponse.sdkHttpResponse().statusCode());
            }

            uploadedToS3 = true;

            document.setFileKey(keyName);
            document.setStatus(DocumentStatus.UPLOADED);
            document.setExpiryDate(null);

            Document savedDocument = documentRepository.saveAndFlush(document);

            return new DocumentUploadResponse(
                    savedDocument.getId(),
                    savedDocument.getDocType().name(),
                    savedDocument.getStatus(),
                    savedDocument.getCreatedAt()
            );

        } catch (Exception e) {

            if (uploadedToS3) {
                try {
                    s3Client.deleteObject(builder -> builder
                            .bucket(bucketName)
                            .key(keyName)
                    );
                } catch (Exception cleanupException) {

                    System.err.println("Failed to cleanup uploaded S3 object: " + keyName);
                }
            }

                throw new DocumentUploadException("Upload failed", e);
            }
    }





    /**
     * Generated presigned Url for a specific file path
     * @param objectKey file path
     * @return String url
     */
    public String generatePresignedUrl(String objectKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .responseContentDisposition("inline")
                .responseContentType("application/pdf")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationTime))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public Long getExpiration() {
        return  this.expirationTime;
    }
}
