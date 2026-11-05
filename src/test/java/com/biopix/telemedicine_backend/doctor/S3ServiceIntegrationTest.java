package com.biopix.telemedicine_backend.doctor;

import com.biopix.telemedicine_backend.doctor.internal.dto.DocumentUploadResponse;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DocumentAlreadyApprovedException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.RegistrationAlreadyApprovedException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.SpecialityRepository;
import com.biopix.telemedicine_backend.doctor.internal.storage.S3Service;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ApplicationModuleTest
@WithPostgresContainer
@TestPropertySource(properties = {
        "aws.s3.bucket=local-demo-test-bucket"
})
@RequiredArgsConstructor
@Tag("aws-integration")
@EnabledIfEnvironmentVariable(named = "RUN_AWS_TESTS", matches = "true")
class S3ServiceAwsIntegrationTest {

    private final S3Service s3Service;

    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;
    private final RegistrationRepository registrationRepository;
    private final DocumentRepository documentRepository;

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private Doctor testDoctor;
    private Registration testRegistration;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        documentRepository.deleteAllInBatch();
        registrationRepository.deleteAllInBatch();
        doctorRepository.deleteAllInBatch();

        testDoctor = doctorRepository.saveAndFlush(Doctor.builder()
                .accountId(1L)
                .firstName("FirstName")
                .lastName("LastName")
                .email("doctor@test.com")
                .dateOfBirth(LocalDate.of(2002, 1, 1))
                .phone("6978582192")
                .gender("MALE")
                .address("Heraklion")
                .country("Greece")
                .licenseNumber("1232131241412")
                .clinicAddress("Heraklion")
                .clinicPhone("210000000")
                .speciality(specialityRepository.findByName("Pediatrician"))
                .build()
        );

        testRegistration = registrationRepository.saveAndFlush(Registration.builder()
                .doctor(testDoctor)
                .reviewResult(ReviewResult.PENDING)
                .build()
        );

        mockFile = new MockMultipartFile(
                "file",
                "diploma_v1.pdf",
                "application/pdf",
                "real aws integration test file".getBytes()
        );
    }

    @Test
    void uploadFile_Success_ShouldUploadRealFileToS3AndUpdateDatabase() {
        Document pendingDocument = documentRepository.saveAndFlush(Document.builder()
                .registration(testRegistration)
                .docType(DocumentType.MEDICAL_DEGREE)
                .status(DocumentStatus.PENDING)
                .build()
        );

        DocumentUploadResponse response = s3Service.uploadFile(
                mockFile,
                "MEDICAL_DEGREE",
                testDoctor.getEmail()
        );

        assertThat(response).isNotNull();
        assertThat(response.documentId()).isEqualTo(pendingDocument.getId());
        assertThat(response.documentStatus()).isEqualTo(DocumentStatus.UPLOADED);

        Document updatedDocument = documentRepository.findById(pendingDocument.getId())
                .orElseThrow();

        assertThat(updatedDocument.getStatus()).isEqualTo(DocumentStatus.UPLOADED);
        assertThat(updatedDocument.getFileKey()).isNotBlank();
        assertThat(updatedDocument.getFileKey()).startsWith(testRegistration.getId() + "/MEDICAL_DEGREE/");
        assertThat(updatedDocument.getFileKey()).endsWith("_diploma_v1.pdf");
        assertThat(updatedDocument.getExpiryDate()).isNull();

        String uploadedFileKey = updatedDocument.getFileKey();

        s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(uploadedFileKey)
                        .build()
        );

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(uploadedFileKey)
                        .build()
        );
    }

    @Test
    void uploadFile_ThrowsException_WhenRegistrationIsAlreadyApproved() {
        testRegistration.setReviewResult(ReviewResult.APPROVED_BY_ADMIN);
        testRegistration.setReviewedByAdminId(99L);
        testRegistration.setReviewedAt(Instant.now());
        testRegistration.setReviewComment("Approved by admin");
        registrationRepository.saveAndFlush(testRegistration);

        assertThatThrownBy(() ->
                s3Service.uploadFile(mockFile, "MEDICAL_DEGREE", testDoctor.getEmail())
        ).isInstanceOf(RegistrationAlreadyApprovedException.class);
    }

    @Test
    void uploadFile_ThrowsException_WhenDocumentIsActiveAndApproved() {
        documentRepository.saveAndFlush(Document.builder()
                .registration(testRegistration)
                .docType(DocumentType.MEDICAL_DEGREE)
                .status(DocumentStatus.APPROVED)
                .fileKey("existing/medical_degree/diploma_v1.pdf")
                .expiryDate(LocalDate.now().plusYears(1))
                .build()
        );

        assertThatThrownBy(() ->
                s3Service.uploadFile(mockFile, "MEDICAL_DEGREE", testDoctor.getEmail())
        )
                .isInstanceOf(DocumentAlreadyApprovedException.class)
                .hasMessageContaining("Cannot re-upload an active, approved document.");
    }
}