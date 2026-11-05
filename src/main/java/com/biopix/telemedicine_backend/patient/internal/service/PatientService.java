package com.biopix.telemedicine_backend.patient.internal.service;

import com.biopix.telemedicine_backend.patient.internal.dto.PatientBasicDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientFullDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.patient.internal.dto.PatientInsertProfileDetailsResponse;
import com.biopix.telemedicine_backend.patient.internal.entity.Patient;
import com.biopix.telemedicine_backend.patient.internal.exceptions.PatientNotFoundException;
import com.biopix.telemedicine_backend.patient.internal.exceptions.PatientProfileAlreadyExistsException;
import com.biopix.telemedicine_backend.patient.internal.repo.PatientRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public PatientInsertProfileDetailsResponse savePatientProfile(Long accountId, String email, PatientInsertProfileDetailsRequest request) {

        if (patientRepository.existsByEmail(email)) {
            throw new PatientProfileAlreadyExistsException();
        }

        Patient patient = Patient.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .dateOfBirth(request.dateOfBirth())
                .phone(request.phone())
                .gender(request.gender())
                .address(request.address())
                .country(request.country())
                .build();

        Patient savedPatient = patientRepository.save(patient);

        //Publish profile created event
        applicationEventPublisher.publishEvent(
                new ProfileCompletedEvent(accountId,email, "PATIENT")
        );

        return new PatientInsertProfileDetailsResponse(
                savedPatient.getId(),
                savedPatient.getEmail(),
                savedPatient.getFirstName(),
                savedPatient.getLastName()
        );
    }


    /**
     * Gets only the important patient details
     * @param id
     * @return
     */
    public PatientBasicDetailsResponse getBasicDetails(Long id) {

        Patient patient = patientRepository.findByAccountId(id);

        if (patient == null) {
            throw new PatientNotFoundException();
        }

        PatientBasicDetailsResponse response = new PatientBasicDetailsResponse(
                patient.getId(),
                patient.getEmail(),
                patient.getFirstName(),
                patient.getLastName()
        );

        return response;

    }

    /**
     * Gets all patient profile details
     * @param id
     * @return
     */
    public PatientFullDetailsResponse getFullDetails(Long id) {

        Patient patient = patientRepository.findByAccountId(id);

        if (patient == null) {
            throw new PatientNotFoundException();
        }

        PatientFullDetailsResponse response = new PatientFullDetailsResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getDateOfBirth(),
                patient.getPhone(),
                patient.getGender(),
                patient.getAddress(),
                patient.getCountry(),
                patient.getRole(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );

        return response;
    }



}
