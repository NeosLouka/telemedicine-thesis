package com.biopix.telemedicine_backend.auth.internal.service;

import com.biopix.telemedicine_backend.auth.internal.exceptions.TooManyAttemptsException;
import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.CaffeineOtpCacheStore;
import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.OtpCacheStore;
import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.dto.OtpData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int length = 6;

    private static final int OTP_ATTEMPTS = 3;

    private final OtpCacheStore otpCacheStore;




    private static String generateOtp() {
        StringBuilder otp = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // Generate a secure random digit from 0 to 9
            otp.append(secureRandom.nextInt(10));
        }


        return otp.toString();
    }

    private static String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    otp.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }


    public String createOtpForUser(@NotBlank @Email(message = "Must be a valid email address") String email) {

        String otp = generateOtp();
        String otpHash = hashOtp(otp);

        //Store OtpHash
        OtpData otpData = new OtpData(otpHash);
        otpCacheStore.put(email,otpData);

        return otp;
    }

    public boolean validateOtpCode(String email, String otpCode ) {

        OtpData otpData = otpCacheStore.get(email);

        if (otpData == null) {
            return false;
        }

        int attempts = otpData.attempts() + 1;

        if (attempts > OTP_ATTEMPTS) {
            otpCacheStore.delete(email);
            throw new TooManyAttemptsException();
        }

        otpCacheStore.put(email, new OtpData(otpData.codeHash(), attempts));

        String insertedCodeHash = hashOtp(otpCode);
        if (!insertedCodeHash.equals(otpData.codeHash())) {
            return false;
        }

        otpCacheStore.delete(email);
        return true;

    }
}
