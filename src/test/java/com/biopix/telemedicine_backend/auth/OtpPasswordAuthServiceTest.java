package com.biopix.telemedicine_backend.auth;


import com.biopix.telemedicine_backend.auth.internal.dto.ChangePasswordResponse;
import com.biopix.telemedicine_backend.auth.internal.dto.ForgotPasswordResponse;
import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.exceptions.InvalidOtpCodeException;
import com.biopix.telemedicine_backend.auth.internal.exceptions.InvalidPasswordException;
import com.biopix.telemedicine_backend.auth.internal.repo.AuthAccountRepository;
import com.biopix.telemedicine_backend.auth.internal.service.AuthService;
import com.biopix.telemedicine_backend.auth.internal.service.OtpService;
import com.biopix.telemedicine_backend.notification.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpPasswordAuthServiceTest {

    @Mock
    private AuthAccountRepository authRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private static final String EMAIL = "user@example.com";
    private static final String OLD_HASH = "hashed_old_password";
    private static final String NEW_PASSWORD = "NewPassword123!";
    private static final String OTP_CODE = "123456";

    private AuthAccount account;

    @BeforeEach
    void setUp() {
        account = new AuthAccount();
        account.setEmail(EMAIL);
        account.setPasswordHash(OLD_HASH);
    }

    // --- forgotPassword Tests ---

    @Test
    void forgotPassword_ShouldSendEmail_WhenAccountExists() {
        when(authRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(otpService.createOtpForUser(EMAIL)).thenReturn(OTP_CODE);

        ForgotPasswordResponse response = authService.forgotPassword(EMAIL);

        assertThat(response).isNotNull();
        verify(emailNotificationService).sendOtpCodeEmail(EMAIL, OTP_CODE);
    }

    @Test
    void forgotPassword_ShouldNotSendEmail_WhenAccountNotFound() {
        when(authRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        ForgotPasswordResponse response = authService.forgotPassword(EMAIL);

        assertThat(response).isNotNull();
        verifyNoInteractions(otpService);
        verifyNoInteractions(emailNotificationService);
    }

    // --- changePassword Tests ---

    @Test
    void changePassword_ShouldUpdatePassword_WhenValid() {
        when(authRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(NEW_PASSWORD, OLD_HASH)).thenReturn(false);
        when(otpService.validateOtpCode(EMAIL, OTP_CODE)).thenReturn(true);
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("new_encoded_hash");

        ChangePasswordResponse response = authService.changePassword(EMAIL, NEW_PASSWORD, OTP_CODE);

        assertThat(response).isNotNull();
        verify(authRepository).save(account);
        assertThat(account.getPasswordHash()).isEqualTo("new_encoded_hash");
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordIsSameAsOld() {
        when(authRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(NEW_PASSWORD, OLD_HASH)).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword(EMAIL, NEW_PASSWORD, OTP_CODE))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("New password cannot be the same");

        verifyNoInteractions(otpService);
    }

    @Test
    void changePassword_ShouldThrowException_WhenOtpIsInvalid() {
        when(authRepository.findByEmail(EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(NEW_PASSWORD, OLD_HASH)).thenReturn(false);
        when(otpService.validateOtpCode(EMAIL, OTP_CODE)).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(EMAIL, NEW_PASSWORD, OTP_CODE))
                .isInstanceOf(InvalidOtpCodeException.class)
                .hasMessageContaining("Invalid Otp code.");

        verify(authRepository, never()).save(any());
    }
}