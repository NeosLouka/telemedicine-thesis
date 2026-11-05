package com.biopix.telemedicine_backend.auth.internal.controller;

import com.biopix.telemedicine_backend.auth.AuthenticatedUser;
import com.biopix.telemedicine_backend.auth.internal.dto.*;
import com.biopix.telemedicine_backend.auth.internal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest request) {

            RegistrationResponse response = authService.register(request);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/register/admin")
        public ResponseEntity<RegistrationResponse> registerAdmin(@RequestBody AdminRegistrationRequest request){

            RegistrationResponse response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal AuthenticatedUser  authenticatedUser,
            @RequestBody(required = false) LogoutRequest logoutRequest
    ){

        Long accountId = authenticatedUser.getUserId();

        authService.logout(accountId, logoutRequest.fcmToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest passwordResetRequest
    ){

        ForgotPasswordResponse response = authService.forgotPassword(passwordResetRequest.email());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request){

        ChangePasswordResponse response = authService.changePassword(request.email(), request.newPassword(), request.otpCode());

        return ResponseEntity.ok(response);

    }
}
