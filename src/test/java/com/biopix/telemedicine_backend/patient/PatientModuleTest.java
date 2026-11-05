package com.biopix.telemedicine_backend.patient;


import com.biopix.telemedicine_backend.patient.internal.dto.PatientInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.patient.internal.entity.Patient;
import com.biopix.telemedicine_backend.patient.internal.repo.PatientRepository;
import com.biopix.telemedicine_backend.patient.internal.service.PatientService;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
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
public class PatientModuleTest {

    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @BeforeEach
    void cleanDatabase() {
        patientRepository.deleteAll();
    }

    @Test
    void savePatientProfile_shouldCreatePatientProfileAndPublishProfileCompletedEvent(Scenario scenario) {
        Long accountId = 1L;
        String email = "patient@test.com";


        PatientInsertProfileDetailsRequest request = new PatientInsertProfileDetailsRequest(
                "FirstName",
                "LastName",
                LocalDate.of(2002, 1, 1),
                "6900000000",
                "MALE",
                "Heraklion",
                "Greece"
        );

        scenario.stimulate(() -> patientService.savePatientProfile(accountId, email, request))
                .andWaitForEventOfType(ProfileCompletedEvent.class)
                .matching(event -> event.accountId().equals(accountId) && event.role().equals("PATIENT"))
                .toArriveAndVerify(event -> {
                    assertThat(event.accountId()).isEqualTo(accountId);
                    assertThat(event.role()).isEqualTo("PATIENT");


                    Patient patient = patientRepository.findByAccountId(accountId);

                    assertThat(patient.getAccountId()).isEqualTo(accountId);
                    assertThat(patient.getFirstName()).isEqualTo("FirstName");
                    assertThat(patient.getLastName()).isEqualTo("LastName");
                    assertThat(patient.getEmail()).isEqualTo(email);
                    assertThat(patient.getDateOfBirth()).isEqualTo(LocalDate.of(2002, 1, 1));
                    assertThat(patient.getPhone()).isEqualTo("6900000000");
                    assertThat(patient.getAddress()).isEqualTo("Heraklion");
                    assertThat(patient.getCountry()).isEqualTo("Greece");

                });
    }

}
