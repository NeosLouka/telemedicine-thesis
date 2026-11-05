package com.biopix.telemedicine_backend.payment.internal.service;

import com.biopix.telemedicine_backend.payment.PaymentService;
import com.biopix.telemedicine_backend.payment.internal.entity.ConsultationPayment;
import com.biopix.telemedicine_backend.payment.internal.entity.DoctorPaymentAccount;
import com.biopix.telemedicine_backend.payment.internal.entity.PatientPaymentAccount;
import com.biopix.telemedicine_backend.payment.internal.repo.ConsultationPaymentRepository;
import com.biopix.telemedicine_backend.payment.internal.repo.DoctorPaymentAccountRepository;
import com.biopix.telemedicine_backend.payment.internal.repo.PatientPaymentAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockPaymentService implements PaymentService {

    private final DoctorPaymentAccountRepository doctorRepository;
    private final PatientPaymentAccountRepository patientRepository;
    private final PatientPaymentAccountRepository patientPaymentAccountRepository;
    private final DoctorPaymentAccountRepository doctorPaymentAccountRepository;
    private final ConsultationPaymentRepository consultationPaymentRepository;


    @Override
    public boolean hasPaymentAccount(Long accountId, String role) {

        return switch (role.toUpperCase()) {
            case "PATIENT" -> patientRepository.existsByAccountId(accountId);
            case "DOCTOR" -> doctorRepository.existsByAccountId(accountId);
            default -> false;
        };

    }

    @Override
    public String createPaymentAccountSetup(
            Long accountId,
            String role
    ) {
        switch (role.toUpperCase()) {
            case "PATIENT" :
                PatientPaymentAccount patient = PatientPaymentAccount.builder()
                        .accountId(accountId)
                        .externalAccountId(generateRandomLong())
                        .build();

                patientRepository.save(patient);

                log.info("Mock payment account created for {} {}", role, accountId);



                break;
            case "DOCTOR" :
                DoctorPaymentAccount doctorPaymentAccount = DoctorPaymentAccount.builder()
                        .accountId(accountId)
                        .externalAccountId(generateRandomLong())
                        .build();

                doctorRepository.save(doctorPaymentAccount);

                log.info("Mock payment account created for {} {}", role, accountId);



                break;
        }
        return "http://localhost:3000/mock-payment/setup";
    }

    @Transactional
    public ConsultationPayment createPaymentForConsultation(
            Long consultationId,
            Long patientAccountId,
            Long doctorAccountId,
            BigDecimal price) {

        PatientPaymentAccount patientPaymentAccount = patientPaymentAccountRepository
                .findByAccountId(patientAccountId);
        DoctorPaymentAccount doctorPaymentAccount = doctorPaymentAccountRepository
                .findByAccountId(doctorAccountId);

        if (patientPaymentAccount == null || doctorPaymentAccount == null) {
            throw new IllegalStateException(
                    "Payment account missing for patient " + patientAccountId
                            + " or doctor " + doctorAccountId
            );
        }

        ConsultationPayment consultationPayment = ConsultationPayment.builder()
                .consultationId(consultationId)
                .patientAccountId(patientAccountId)
                .doctorAccountId(doctorAccountId)
                .patientPaymentAccountId(patientPaymentAccount.getId())
                .doctorPaymentAccountId(doctorPaymentAccount.getId())
                .price(price)
                .externalPaymentId(randomStringId())
                .build();

        ConsultationPayment saved = consultationPaymentRepository.save(consultationPayment);

        log.info("Mock consultation payment created: consultationId={}, patient={}, doctor={}, price={}, status={}, externalPaymentId={}", saved.getConsultationId(), saved.getPatientAccountId(), saved.getDoctorAccountId(), saved.getPrice(), saved.getStatus(), saved.getExternalPaymentId() );

        return saved;
    }



    private String generateRandomLong() {
        Random random = new Random();
        return Long.toString(random.nextLong());
    }

    private String randomStringId(){
        return UUID.randomUUID().toString();
    }

}