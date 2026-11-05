package com.biopix.telemedicine_backend.auth.internal.service;

import com.biopix.telemedicine_backend.auth.internal.dto.*;
import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.enums.AuthRole;
import com.biopix.telemedicine_backend.auth.internal.exceptions.*;
import com.biopix.telemedicine_backend.auth.internal.security.JwtService;
import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.repo.AuthAccountRepository;
import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLoginEvent;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLogoutEvent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthAccountRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;

    private final EmailNotificationService emailNotificationService;

    private final ApplicationEventPublisher publisher;
    public ForgotPasswordResponse resetPassword;
    @Value("${admin.registration-secret}")
    private String secretKey;


    /**
     * Register user. By default, account status is PENDING_PROFILE_COMPLETION
     * @param request
     * @return RegistrationsResponse (emai, role)
     */
    public RegistrationResponse register(RegistrationRequest request) {

        if (request.role() == AuthRole.ADMIN) {
            throw new InvalidRegistrationRoleException("Invalid role. Admin is not allowed from this endpoint.");
        }

        if(authRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException();
        }
        AuthAccount authAccount = AuthAccount.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        AuthAccount savedAccount = authRepository.save(authAccount);

        // Issue standard access token with built-in custom scope mappings
        String token = jwtService.generateToken(savedAccount);

        return new RegistrationResponse(
                token,
                savedAccount.getId(),
                savedAccount.getEmail(),
                savedAccount.getRole(),
                savedAccount.getAccountStatus()
        );
    }


    /**
     * Authenticate user and issue state token
     * @param request credentials
     * @return LoginResponse containing signed JWT token strings
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        AuthAccount account = authRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Cross-check row hashes securely
        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Issue standard access token with built-in custom scope mappings
        String token = jwtService.generateToken(account);

        //If account is ACTIVE then change availability
        if(account.getRole().equals(AuthRole.DOCTOR) && account.getAccountStatus() == AccountStatus.ACTIVE ) {
            publisher.publishEvent(new SuccessfulLoginEvent(
                    account.getId(),
                    account.getEmail(),
                    account.getRole().name()
            ));
        }

        return new LoginResponse(
                token,
                account.getId(),
                account.getEmail(),
                account.getRole(),
                account.getAccountStatus()
        );
    }

    public RegistrationResponse registerAdmin(AdminRegistrationRequest request) {

        if (request.role() != AuthRole.ADMIN) {
            throw new InvalidRegistrationRoleException("Invalid role. Only Admin is allowed from this endpoint.");
        }

        if (authRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (!request.secretKey().equals(secretKey)) {
            throw new InvalidAdminSecretException();
        }

        AuthAccount authAccount = AuthAccount.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        AuthAccount savedAccount = authRepository.save(authAccount);

        // Issue standard access token with built-in custom scope mappings
        String token = jwtService.generateToken(savedAccount);


        return new RegistrationResponse(
                token,
                savedAccount.getId(),
                savedAccount.getEmail(),
                savedAccount.getRole(),
                savedAccount.getAccountStatus()
        );
    }

    @Transactional
    public void logout(Long accountId , String fcmToken) {

        AuthAccount account = authRepository.findById(accountId).orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if(account.getRole().equals(AuthRole.DOCTOR) && account.getAccountStatus() == AccountStatus.ACTIVE) {
            publisher.publishEvent(new SuccessfulLogoutEvent(
                    account.getId(),
                    account.getEmail(),
                    account.getRole().name(),
                    fcmToken
            ));
        }

    }


    public ForgotPasswordResponse forgotPassword(@NotBlank @Email(message = "Must be a valid email address") String email) {

        AuthAccount account = authRepository.findByEmail(email)
                .orElse(null);

        if(account == null) {
            return new ForgotPasswordResponse();
        }

        String code = otpService.createOtpForUser(email);

        //Send Email with OTP_Code to user

        try {
            emailNotificationService.sendOtpCodeEmail(email, code);
        }catch(Exception e) {
            log.error("Failed to send OTP email to {}", email, e);
        }

        return new ForgotPasswordResponse();


    }

    @Transactional
    public ChangePasswordResponse changePassword(String email, String newPassword, String otpCode) {

        AuthAccount account = authRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Account not found"));


        //Change password
        if(passwordEncoder.matches(newPassword, account.getPasswordHash())) {
            throw new InvalidPasswordException("New password cannot be the same as current password.");
        }

        //Validate Otp code
        boolean validationResult = otpService.validateOtpCode(email, otpCode);

        if(!validationResult) {
            throw new InvalidOtpCodeException("Invalid Otp code.");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        account.setUpdatedAt(Instant.now());
        authRepository.save(account);

        return ChangePasswordResponse.valid();
    }
}
