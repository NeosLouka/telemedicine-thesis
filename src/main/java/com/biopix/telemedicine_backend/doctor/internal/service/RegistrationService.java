package com.biopix.telemedicine_backend.doctor.internal.service;


import com.biopix.telemedicine_backend.doctor.internal.dto.RegistrationCreateResponse;
import com.biopix.telemedicine_backend.doctor.internal.dto.RegistrationDetailsResponse;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DoctorNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.RegistrationAlreadyExistsException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.RegistrationNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@AllArgsConstructor
@Service
public class RegistrationService {

    private final DoctorRepository doctorRepository;
    private final RegistrationRepository registrationRepository;
    private final DocumentRepository documentRepository;


    /**
     * Creates registration
     * @param email
     * @return
     */
    @Transactional
    public RegistrationCreateResponse create(String email) {

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            throw new DoctorNotFoundException();
        }

        if (registrationRepository.existsByDoctorId(doctor.getId())) {
            throw new RegistrationAlreadyExistsException();
        }

        Registration registration = Registration.builder()
                .doctor(doctor)
                .reviewedAt(null)
                .reviewedByAdminId(null)
                .reviewComment(null)
                .build();

        Registration savedRegistration = registrationRepository.save(registration);


        for(DocumentType documentType : DocumentType.values()){
            Document pendingDoc = Document.builder()
                    .registration(savedRegistration)
                    .docType(documentType)
                    .build();

            documentRepository.save(pendingDoc);
        }


        return new RegistrationCreateResponse(
                savedRegistration.getId(),
                savedRegistration.getDoctor().getId(),
                savedRegistration.getDoctor().getEmail(),
                savedRegistration.getReviewResult(),
                savedRegistration.getCreatedAt()
        );
    }

    public RegistrationDetailsResponse getDetails(String email) {

        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null) {
            throw new DoctorNotFoundException();
        }

        Registration registration = registrationRepository.findByDoctorId(doctor.getId());

        if (registration == null) {
            throw new RegistrationNotFoundException();
        }

        RegistrationDetailsResponse response = new RegistrationDetailsResponse(
                registration.getId(),
                registration.getDoctor().getEmail(),
                registration.getReviewedAt(),
                registration.getReviewedByAdminId(),
                registration.getReviewResult(),
                registration.getReviewComment(),
                registration.getCreatedAt(),
                registration.getUpdatedAt()
        );

        return response;
    }
}
