package com.biopix.telemedicine_backend.doctor;

import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import com.biopix.telemedicine_backend.doctor.internal.dto.DoctorInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.DocumentRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.RegistrationRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.SpecialityRepository;
import com.biopix.telemedicine_backend.doctor.internal.service.DoctorService;
import com.biopix.telemedicine_backend.doctor.internal.service.RegistrationService;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLoginEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLogoutEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
public class DoctorModuleTest {

    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;
    private final RegistrationRepository registrationRepository;
    private final DocumentRepository documentRepository;


    @BeforeEach
    void cleanDatabase() {
        documentRepository.deleteAll();
        registrationRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    void saveDoctorProfile_shouldCreateDoctorProfileAndPublishProfileCompletedEvent(Scenario scenario) {
        Long accountId = 1L;
        String email = "doctor@test.com";
        String specialityName = "Pediatrician";

        assertThat(specialityRepository.findByName(specialityName)).isNotNull();

        DoctorInsertProfileDetailsRequest request = new DoctorInsertProfileDetailsRequest(
                "DoctorFirstName",
                "DoctorLastName",
                LocalDate.of(2002, 1, 1),
                "6900000000",
                "MALE",
                "Heraklion",
                "Greece",
                "124122342351",
                "Clinic Address",
                "2810000000",
                specialityName
        );

        scenario.stimulate(() -> doctorService.saveDoctorProfile(accountId, email, request))
                .andWaitForEventOfType(ProfileCompletedEvent.class)
                .matching(event -> event.accountId().equals(accountId) && event.role().equals("DOCTOR"))
                .toArriveAndVerify(event -> {
                    assertThat(event.accountId()).isEqualTo(accountId);
                    assertThat(event.role()).isEqualTo("DOCTOR");

                    Doctor doctor = doctorRepository.findByEmail(email);

                    if (doctor == null) {
                        throw new AssertionError("Doctor Profile could not be found");
                    }
                    assertThat(doctor.getAccountId()).isEqualTo(accountId);
                    assertThat(doctor.getFirstName()).isEqualTo("DoctorFirstName");
                    assertThat(doctor.getLastName()).isEqualTo("DoctorLastName");
                    assertThat(doctor.getEmail()).isEqualTo(email);
                    assertThat(doctor.getDateOfBirth()).isEqualTo(LocalDate.of(2002, 1, 1));
                    assertThat(doctor.getPhone()).isEqualTo("6900000000");
                    assertThat(doctor.getAddress()).isEqualTo("Heraklion");
                    assertThat(doctor.getCountry()).isEqualTo("Greece");
                    assertThat(doctor.getLicenseNumber()).isEqualTo("124122342351");
                    assertThat(doctor.getClinicAddress()).isEqualTo("Clinic Address");
                    assertThat(doctor.getClinicPhone()).isEqualTo("2810000000");
                    assertThat(doctor.getAvailabilityStatus()).isEqualTo(DoctorAvailabilityStatus.OFFLINE);

                    assertThat(doctor.getSpeciality()).isNotNull();
                    assertThat(doctor.getSpeciality().getName()).isEqualTo(specialityName);
                });


    }

    @Test
    void successfulLoginEvent_shouldSetDoctorAvailabilityOnline(Scenario scenario) {

        Long accountId = 1L;
        String email = "doctor@test.com";

        Doctor doctor = Doctor.builder()
                .accountId(accountId)
                .firstName("FirstNane")
                .lastName("LastName")
                .email(email)
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

        doctor.setAvailabilityStatus(DoctorAvailabilityStatus.OFFLINE);

        doctorRepository.saveAndFlush(doctor);

        SuccessfulLoginEvent event = new SuccessfulLoginEvent(
                accountId,
                email,
                AuthRole.DOCTOR.name()
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        doctorRepository.findByAccountId(accountId)
                                .filter(savedDoctor ->
                                        savedDoctor.getAvailabilityStatus()
                                                == DoctorAvailabilityStatus.ONLINE
                                )
                )
                .andVerify(optionalDoctor -> {
                    assertThat(optionalDoctor).isPresent();

                    Doctor updatedDoctor = optionalDoctor.get();

                    assertThat(updatedDoctor.getAccountId()).isEqualTo(accountId);
                    assertThat(updatedDoctor.getEmail()).isEqualTo(email);
                    assertThat(updatedDoctor.getAvailabilityStatus())
                            .isEqualTo(DoctorAvailabilityStatus.ONLINE);
                });
    }


    @Test
    void successfulLogoutEvent_shouldSetDoctorAvailabilityOffline(Scenario scenario) {

        Long accountId = 1L;
        String email = "doctor@test.com";


        Doctor doctor = Doctor.builder()
                .accountId(accountId)
                .firstName("FirstNane")
                .lastName("LastName")
                .email(email)
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

        doctor.setAvailabilityStatus(DoctorAvailabilityStatus.ONLINE);
        doctorRepository.saveAndFlush(doctor);

        SuccessfulLogoutEvent event = new SuccessfulLogoutEvent(
                accountId,
                doctor.getEmail(),
                "DOCTOR"
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        doctorRepository.findByAccountId(accountId)
                                .filter(savedDoctor ->
                                        savedDoctor.getAvailabilityStatus() == DoctorAvailabilityStatus.OFFLINE
                                )
                )
                .andVerify(optionalDoctor -> {
                    assertThat(optionalDoctor).isPresent();

                    Doctor updatedDoctor = optionalDoctor.get();

                    assertThat(updatedDoctor.getAvailabilityStatus())
                            .isEqualTo(DoctorAvailabilityStatus.OFFLINE);
                });
    }


}
