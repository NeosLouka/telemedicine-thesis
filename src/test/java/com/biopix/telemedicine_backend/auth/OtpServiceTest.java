package com.biopix.telemedicine_backend.auth;


import com.biopix.telemedicine_backend.auth.internal.exceptions.TooManyAttemptsException;
import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.OtpCacheStore;
import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.dto.OtpData;
import com.biopix.telemedicine_backend.auth.internal.service.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpCacheStore otpCacheStore;

    @InjectMocks
    private OtpService otpService;

    private static final String EMAIL = "test@example.com";

    @Test
    void createOtpForUser_ShouldGenerate6DigitCodeAndStoreHash() {
        String generatedOtp = otpService.createOtpForUser(EMAIL);

        assertThat(generatedOtp).hasSize(6).containsOnlyDigits();
        verify(otpCacheStore).put(eq(EMAIL), any(OtpData.class));
    }

    @Test
    void validateOtpCode_ShouldReturnTrue_WhenCodeMatches() {
        String otpCode = otpService.createOtpForUser(EMAIL);
        ArgumentCaptor<OtpData> captor = ArgumentCaptor.forClass(OtpData.class);
        verify(otpCacheStore).put(eq(EMAIL), captor.capture());

        OtpData storedData = captor.getValue();
        when(otpCacheStore.get(EMAIL)).thenReturn(storedData);

        boolean isValid = otpService.validateOtpCode(EMAIL, otpCode);

        assertThat(isValid).isTrue();
        verify(otpCacheStore).delete(EMAIL);
    }

    @Test
    void validateOtpCode_ShouldReturnFalse_WhenCodeIsWrong() {
        OtpData storedData = new OtpData("someHash", 0);
        when(otpCacheStore.get(EMAIL)).thenReturn(storedData);

        boolean isValid = otpService.validateOtpCode(EMAIL, "999999");

        assertThat(isValid).isFalse();
        verify(otpCacheStore).put(eq(EMAIL), any(OtpData.class));
        verify(otpCacheStore, never()).delete(EMAIL);
    }

    @Test
    void validateOtpCode_ShouldThrowTooManyAttemptsException_WhenExceedingLimit() {
        OtpData storedData = new OtpData("someHash", 3);
        when(otpCacheStore.get(EMAIL)).thenReturn(storedData);

        assertThatThrownBy(() -> otpService.validateOtpCode(EMAIL, "123456"))
                .isInstanceOf(TooManyAttemptsException.class);

        verify(otpCacheStore).delete(EMAIL);
    }
}