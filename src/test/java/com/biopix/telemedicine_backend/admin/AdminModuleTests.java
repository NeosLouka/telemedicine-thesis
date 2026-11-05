package com.biopix.telemedicine_backend.admin;


import com.biopix.telemedicine_backend.admin.internal.dto.AdminInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.admin.internal.dto.ReviewRegistrationRequest;
import com.biopix.telemedicine_backend.admin.internal.entity.Admin;
import com.biopix.telemedicine_backend.admin.internal.enums.ReviewResult;
import com.biopix.telemedicine_backend.admin.internal.repo.AdminRepository;
import com.biopix.telemedicine_backend.admin.internal.service.AdminReviewService;
import com.biopix.telemedicine_backend.admin.internal.service.AdminService;
import com.biopix.telemedicine_backend.doctor.DoctorServiceApi;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.RegistrationReviewSubmittedEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
public class AdminModuleTests {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final AdminReviewService adminReviewService;

    @MockitoBean
    private DoctorServiceApi doctorServiceApi;


    @BeforeEach
    void cleanDatabase() {
        adminRepository.deleteAll();
    }

    @Test
    void saveAdminProfile_shouldCreateAdminProfileAndPublishProfileCompletedEvent(Scenario scenario){
        Long accountId = 1L;
        String email = "admin@test.com";

        AdminInsertProfileDetailsRequest request = new AdminInsertProfileDetailsRequest(
                "AdminFirstName",
                "AdminLastName",
                "693024033",
                "Biopix-T"
        );

        scenario.stimulate(() -> adminService.saveAdminProfile(accountId, email, request))
                .andWaitForEventOfType(ProfileCompletedEvent.class)
                .matching(event -> event.accountId().equals(accountId) && event.role().equals("ADMIN"))
                .toArriveAndVerify(event -> {

                    assertThat(event.accountId()).isEqualTo(accountId);
                    assertThat(event.role()).isEqualTo("ADMIN");

                    Admin admin = adminRepository.findByEmail(email)
                            .orElseThrow(() -> new AssertionError("Admin Profile could not be found"));

                    assertThat(admin.getAccountId()).isEqualTo(accountId);
                    assertThat(admin.getFirstName()).isEqualTo("AdminFirstName");
                    assertThat(admin.getLastName()).isEqualTo("AdminLastName");
                    assertThat(admin.getEmail()).isEqualTo(email);
                    assertThat(admin.getPhoneNumber()).isEqualTo("693024033");
                    assertThat(admin.getDepartment()).isEqualTo("Biopix-T");
                });

    }

    @Test
    void reviewRegistration_shouldPublishRegistrationReviewSubmittedEvent(Scenario scenario) {
        Admin admin = saveAdmin("admin@test.com");

        Long registrationId = 10L;

        ReviewRegistrationRequest request = new ReviewRegistrationRequest(
                ReviewResult.INFO_REQUESTED,
                "Please upload clearer documents."
        );

        scenario.stimulate(() -> adminReviewService.reviewRegistration(registrationId, admin.getEmail(), request))
                .andWaitForEventOfType(RegistrationReviewSubmittedEvent.class)
                .matching(event ->
                        event.registrationId().equals(registrationId) && event.adminId().equals(admin.getId()) && event.reviewResult().equals(ReviewResult.INFO_REQUESTED.name())
                )
                .toArriveAndVerify(event -> {
                    assertThat(event.registrationId()).isEqualTo(registrationId);
                    assertThat(event.adminId()).isEqualTo(admin.getId());
                    assertThat(event.reviewResult()).isEqualTo("INFO_REQUESTED");
                    assertThat(event.reviewComment()).isEqualTo("Please upload clearer documents.");
                    assertThat(event.reviewedAt()).isNotNull();
                });
    }


    @Test
    void reviewRegistration_withApprovedByAdmin_shouldThrowException() {
        Admin admin = saveAdmin("admin@test.com");

        ReviewRegistrationRequest request = new ReviewRegistrationRequest(
                ReviewResult.APPROVED_BY_ADMIN,
                "Approved."
        );

        assertThatThrownBy(() -> adminReviewService.reviewRegistration(10L, admin.getEmail(), request))
                .isInstanceOf(RuntimeException.class);
    }

    private Admin saveAdmin(String email) {
        Admin admin = Admin.builder()
                .accountId(1L)
                .firstName("AdminFirstName")
                .lastName("AdminLastName")
                .email(email)
                .phoneNumber("693024033")
                .department("Biopix-T")
                .build();

        return adminRepository.saveAndFlush(admin);
    }

}
