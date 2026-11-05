package com.biopix.telemedicine_backend.doctor.internal.service;

import com.biopix.telemedicine_backend.doctor.internal.dto.*;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DoctorNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DoctorProfileAlreadyExistsException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.SpecialityRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class DoctorService {


    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public DoctorInsertProfileDetailsResponse saveDoctorProfile(Long accountId, String email, DoctorInsertProfileDetailsRequest request) {

        if (doctorRepository.existsByEmail(email)) {
            throw new DoctorProfileAlreadyExistsException();
        }

        Doctor doctor = Doctor.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .dateOfBirth(request.dateOfBirth())
                .phone(request.phone())
                .gender(request.gender())
                .address(request.address())
                .country(request.country())
                .availabilityStatus(DoctorAvailabilityStatus.OFFLINE)
                .licenseNumber(request.licenseNumber())
                .clinicAddress(request.clinicAddress())
                .clinicPhone(request.clinicPhone())
                .speciality(specialityRepository.findByName(request.speciality()))
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        //Publish event for profile creation
        applicationEventPublisher.publishEvent(
                new ProfileCompletedEvent(accountId,email, "DOCTOR")
        );

        return new DoctorInsertProfileDetailsResponse(
                savedDoctor.getId(),
                savedDoctor.getEmail(),
                savedDoctor.getFirstName(),
                savedDoctor.getLastName()
        );
    }

    /**
     * Gets only the important doctor details
     * @param id
     * @return
     */
    public DoctorBasicDetailsResponse getBasicDetails(Long id) {

        Doctor doctor = doctorRepository.findByAccountId(id).orElseThrow(DoctorNotFoundException::new);

        DoctorBasicDetailsResponse response = new DoctorBasicDetailsResponse(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getClinicAddress(),
                doctor.getAvailabilityStatus().equals(DoctorAvailabilityStatus.OFFLINE) ? DoctorAvailabilityStatus.ONLINE.name() : DoctorAvailabilityStatus.OFFLINE.name()
        );

        return response;

    }

    /**
     * Gets all profile doctor details
     * @param id
     * @return
     */
    public DoctorFullDetailsResponse getFullDetails(Long id) {

        Doctor doctor = doctorRepository.findByAccountId(id).orElseThrow(DoctorNotFoundException::new);

        DoctorFullDetailsResponse response = new DoctorFullDetailsResponse(
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

        return response;
    }

    public List<SpecialityResponse> getSpecialities() {

        return specialityRepository.findAll().stream()
                .map(s->new SpecialityResponse(s.getId(), s.getName(), s.getDescription()))
                .toList();

    }

    public AvailabilityStatusResponse changeAvailabilityStatus(Long id ,DoctorAvailabilityStatus status) {

        Doctor doctor = doctorRepository.findByAccountId(id).orElseThrow(DoctorNotFoundException::new);

        doctor.setAvailabilityStatus(status);
        doctorRepository.save(doctor);

        return new AvailabilityStatusResponse(
                doctor.getId(),
                doctor.getAvailabilityStatus()
        );
    }
}
