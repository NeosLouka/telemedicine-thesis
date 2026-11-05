package com.biopix.telemedicine_backend.auth;


import com.biopix.telemedicine_backend.auth.internal.dto.*;
import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import com.biopix.telemedicine_backend.auth.internal.repo.AuthAccountRepository;
import com.biopix.telemedicine_backend.auth.internal.service.AuthService;
import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorRegistrationApprovedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLoginEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLogoutEvent;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ApplicationModuleTest
@WithPostgresContainer
@RequiredArgsConstructor
public class AuthModuleTest {

    private final AuthService authService;
    private final AuthAccountRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @MockitoBean
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    void cleanDatabase() {
        authRepository.deleteAll();
    }

    @Test
    void registerPatient_shouldCreateAccountWithPendingProfileCompletionStatus(){
        String email = "patient@test.com";
        String password = "SecurePassword123!";
        AuthRole role =  AuthRole.PATIENT;

        RegistrationRequest request = new RegistrationRequest(email, password, role);
        RegistrationResponse response = authService.register(request);

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.accountId()).isNotNull();
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.role().equals(AuthRole.PATIENT));
        assertThat(response.status()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);


        AuthAccount savedAccount = authRepository.findByEmail(email)
                .orElseThrow(()-> new AssertionError("account not found with email: " + email));

        assertThat(savedAccount.getEmail()).isEqualTo(email);
        assertThat(savedAccount.getRole()).isEqualTo(AuthRole.PATIENT);
        assertThat(savedAccount.getAccountStatus()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);

        assertThat(savedAccount.getPasswordHash()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, savedAccount.getPasswordHash())).isTrue();


    }

    @Test
    void registerDoctor_shouldCreateAccountWithPendingProfileCompletionStatus(){
        String email = "doctor@test.com";
        String password = "SecurePassword123!";
        AuthRole role =  AuthRole.DOCTOR;

        RegistrationRequest request = new RegistrationRequest(
                email,
                password,
                role
        );

        RegistrationResponse response = authService.register(request);

        assertThat(response.accessToken()).isNotNull();
        assertThat(response.accountId()).isNotNull();
        assertThat(response.email().equals(email));
        assertThat(response.role().equals(AuthRole.DOCTOR));
        assertThat(response.status()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);


        AuthAccount savedAccount = authRepository.findByEmail(email)
                .orElseThrow(()-> new AssertionError("account not found with email: " + email));

        assertThat(savedAccount.getEmail()).isEqualTo(email);
        assertThat(savedAccount.getRole()).isEqualTo(AuthRole.DOCTOR);
        assertThat(savedAccount.getAccountStatus()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);

        assertThat(savedAccount.getPasswordHash()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, savedAccount.getPasswordHash())).isTrue();


    }
    @Test
    void registerAdmin_shouldCreateAccountWithPendingProfileCompletionStatus(){
        String email = "admin@test.com";
        String password = "SecurePassword123!";
        AuthRole role = AuthRole.ADMIN;
        String secretKey = "local-demo-admin-secret";

        AdminRegistrationRequest request = new AdminRegistrationRequest(
                email,
                password,
                role,
                secretKey

        );

        RegistrationResponse response = authService.registerAdmin(request);

        assertThat(response.accessToken()).isNotNull();
        assertThat(response.accountId()).isNotNull();
        assertThat(response.email().equals(email));
        assertThat(response.role().equals(AuthRole.ADMIN));
        assertThat(response.status()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);


        AuthAccount savedAccount = authRepository.findByEmail(email)
                .orElseThrow(()-> new AssertionError("account not found with email: " + email));

        assertThat(savedAccount.getEmail()).isEqualTo(email);
        assertThat(savedAccount.getRole()).isEqualTo(AuthRole.ADMIN);
        assertThat(savedAccount.getAccountStatus()).isEqualTo(AccountStatus.PENDING_PROFILE_COMPLETION);

        assertThat(savedAccount.getPasswordHash()).isNotEqualTo(password);
        assertThat(passwordEncoder.matches(password, savedAccount.getPasswordHash())).isTrue();


    }

    @Test
    void loginUser_shouldLetRegisteredUserLoginAndReturnValidAccessToken(){
        String email = "patient@test.com";
        String password = "SecurePassword123!";
        AuthRole role =  AuthRole.PATIENT;

        //First save patient in the db
        RegistrationRequest regRequest = new RegistrationRequest(
                email,
                password,
                role
        );

        authService.register(regRequest);

        //Select auth account

        AuthAccount account = authRepository.findByEmail(email)
                .orElseThrow(()-> new AssertionError("account not found with email: " + email));


        //Login request
        LoginRequest request = new LoginRequest(
                email,
                password
        );

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isNotBlank();

        assertThat(response.email()).isEqualTo(email);

        assertThat(response.role()).isEqualTo(AuthRole.PATIENT);

    }

    @Test
    void profileCompletedEvent_forPatient_shouldActivateAccount(Scenario scenario) {
        AuthAccount account = saveAccount(
                "patient@test.com",
                "SecurePassword123!",
                AuthRole.PATIENT,
                AccountStatus.PENDING_PROFILE_COMPLETION
        );

        ProfileCompletedEvent event = new ProfileCompletedEvent(
                account.getId(),
                AuthRole.PATIENT.name()
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        authRepository.findById(account.getId())
                                .filter(savedAccount ->
                                        savedAccount.getAccountStatus() == AccountStatus.ACTIVE
                                )
                )
                .andVerify(optionalAccount -> {
                    assertThat(optionalAccount).isPresent();

                    AuthAccount updatedAccount = optionalAccount.get();

                    assertThat(updatedAccount.getId()).isEqualTo(account.getId());
                    assertThat(updatedAccount.getRole()).isEqualTo(AuthRole.PATIENT);
                    assertThat(updatedAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
                });
    }

    @Test
    void profileCompletedEvent_forDoctor_shouldSetPendingAdminReview(Scenario scenario) {
        AuthAccount account = saveAccount(
                "doctor@test.com",
                "SecurePassword123!",
                AuthRole.DOCTOR,
                AccountStatus.PENDING_PROFILE_COMPLETION
        );

        ProfileCompletedEvent event = new ProfileCompletedEvent(
                account.getId(),
                AuthRole.DOCTOR.name()
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        authRepository.findById(account.getId())
                                .filter(savedAccount ->
                                        savedAccount.getAccountStatus() == AccountStatus.PENDING_ADMIN_REVIEW
                                )
                )
                .andVerify(optionalAccount -> {
                    assertThat(optionalAccount).isPresent();

                    AuthAccount updatedAccount = optionalAccount.get();

                    assertThat(updatedAccount.getId()).isEqualTo(account.getId());
                    assertThat(updatedAccount.getRole()).isEqualTo(AuthRole.DOCTOR);
                    assertThat(updatedAccount.getAccountStatus()).isEqualTo(AccountStatus.PENDING_ADMIN_REVIEW);
                });
    }

    @Test
    void profileCompletedEvent_forAdmin_shouldActivateAccount(Scenario scenario) {
        AuthAccount account = saveAccount(
                "admin@test.com",
                "SecurePassword123!",
                AuthRole.ADMIN,
                AccountStatus.PENDING_PROFILE_COMPLETION
        );

        ProfileCompletedEvent event = new ProfileCompletedEvent(
                account.getId(),
                AuthRole.ADMIN.name()
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        authRepository.findById(account.getId())
                                .filter(savedAccount ->
                                        savedAccount.getAccountStatus() == AccountStatus.ACTIVE
                                )
                )
                .andVerify(optionalAccount -> {
                    assertThat(optionalAccount).isPresent();

                    AuthAccount updatedAccount = optionalAccount.get();

                    assertThat(updatedAccount.getId()).isEqualTo(account.getId());
                    assertThat(updatedAccount.getRole()).isEqualTo(AuthRole.ADMIN);
                    assertThat(updatedAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
                });
    }

    @Test
    void doctorRegistrationApprovedEvent_shouldActivateDoctorAccount(Scenario scenario) {
        AuthAccount account = saveAccount(
                "doctor@test.com",
                "SecurePassword123!",
                AuthRole.DOCTOR,
                AccountStatus.PENDING_ADMIN_REVIEW
        );

        DoctorRegistrationApprovedEvent event = new DoctorRegistrationApprovedEvent(
                account.getId(),
                0L,
                0L
        );

        scenario.publish(event)
                .andWaitForStateChange(() ->
                        authRepository.findById(account.getId())
                                .filter(savedAccount ->
                                        savedAccount.getAccountStatus() == AccountStatus.ACTIVE
                                )
                )
                .andVerify(optionalAccount -> {
                    assertThat(optionalAccount).isPresent();

                    AuthAccount updatedAccount = optionalAccount.get();

                    assertThat(updatedAccount.getId()).isEqualTo(account.getId());
                    assertThat(updatedAccount.getRole()).isEqualTo(AuthRole.DOCTOR);
                    assertThat(updatedAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
                });
    }




    private AuthAccount saveAccount(String email, String password, AuthRole role, AccountStatus status){
        AuthAccount account = AuthAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .build();

        account.setAccountStatus(status);

        return authRepository.saveAndFlush(account);
    }


    @Test
    void loginActiveDoctor_shouldPublishSuccessfulLoginEvent(Scenario scenario) {

        String email = "doctor@test.com";
        String password = "SecurePassword123!";

        AuthAccount account = saveAccount(
                email,
                password,
                AuthRole.DOCTOR,
                AccountStatus.ACTIVE
        );

        LoginRequest request = new LoginRequest(
                email,
                password
        );

        scenario.stimulate(() -> authService.login(request))
                .andWaitForEventOfType(SuccessfulLoginEvent.class)
                .matching(event ->
                        event.userId().equals(account.getId())
                                && event.userEmail().equals(email)
                                && event.userRole().equals(AuthRole.DOCTOR.name())
                )
                .toArriveAndVerify(event -> {
                    assertThat(event.userId()).isEqualTo(account.getId());
                    assertThat(event.userEmail()).isEqualTo(email);
                    assertThat(event.userRole()).isEqualTo(AuthRole.DOCTOR.name());
                });
    }

    @Test
    void logoutActiveDoctor_shouldPublishSuccessfulLogoutEvent(Scenario scenario) {

        String email = "doctor@test.com";
        String password = "SecurePassword123!";

        AuthAccount account = saveAccount(
                email,
                password,
                AuthRole.DOCTOR,
                AccountStatus.ACTIVE
        );

        scenario.stimulate(() -> authService.logout(account.getId(), null))
                .andWaitForEventOfType(SuccessfulLogoutEvent.class)
                .matching(event ->
                        event.id().equals(account.getId())
                                && event.email().equals(email)
                                && event.role().equals(AuthRole.DOCTOR.name())
                )
                .toArriveAndVerify(event -> {
                    assertThat(event.id()).isEqualTo(account.getId());
                    assertThat(event.email()).isEqualTo(email);
                    assertThat(event.role()).isEqualTo(AuthRole.DOCTOR.name());
                });
    }
}
