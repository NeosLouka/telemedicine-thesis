package com.biopix.telemedicine_backend.doctor;


import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.entity.Registration;
import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.SpecialityRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.RegistrationReviewSubmittedEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.within;

@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
class DoctorListenerTest {

    private final RegistrationRepository registrationRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialityRepository  specialityRepository;
    private final DocumentRepository documentRepository;

    @BeforeEach
    void cleanUpDatabase() {
        documentRepository.deleteAll();
        registrationRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    public void shouldUpdateRegistrationOnEvent(Scenario scenario) {

        Doctor doctor = Doctor.builder()
                .accountId(1L)
                .firstName("FirstNane")
                .lastName("LastName")
                .email("doctor@test.com")
                .dateOfBirth(LocalDate.now())
                .phone("6978582192")
                .gender("MALE")
                .address("Heraklion")
                .country("Greece")
                .licenseNumber("1232131241412")
                .clinicAddress("Heraklion")
                .clinicPhone("210000000")
                .speciality(specialityRepository.findById(1L).orElseThrow(()-> new RuntimeException()))
                .build();


        Doctor savedDoctor = doctorRepository.saveAndFlush(doctor);

        Registration registration = Registration.builder()
                .doctor(savedDoctor)
                .build();

        Registration savedRegistration = registrationRepository.saveAndFlush(registration);

        Long registrationId = savedRegistration.getId();
        Long adminId = 99L;
        String comment = "Documents are blurred.";
        Instant now = Instant.now();

        RegistrationReviewSubmittedEvent event = new RegistrationReviewSubmittedEvent(
                registrationId,
                adminId,
                "INFO_REQUESTED",
                comment,
                now
        );

        scenario.publish(event)

                .andWaitForStateChange(
                        ()-> registrationRepository.findById(registrationId).orElse(null),
                        repoReg -> repoReg != null && repoReg.getReviewResult() == ReviewResult.INFO_REQUESTED
                )

                .andVerify(result -> {
                    Registration updatedRegistration = registrationRepository.findById(registrationId)
                            .orElseThrow(() -> new AssertionError("Registration missing after update!"));

                    assertThat(updatedRegistration.getReviewedByAdminId()).isEqualTo(adminId);
                    assertThat(updatedRegistration.getReviewComment()).isEqualTo(comment);
                    assertThat(updatedRegistration.getReviewedAt()).isCloseTo(now, within(1, ChronoUnit.MILLIS));
                });


    }

}