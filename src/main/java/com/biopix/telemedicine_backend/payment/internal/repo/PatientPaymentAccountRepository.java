package com.biopix.telemedicine_backend.payment.internal.repo;

import com.biopix.telemedicine_backend.payment.internal.entity.PatientPaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientPaymentAccountRepository extends JpaRepository<PatientPaymentAccount, Long> {
    boolean existsByAccountId(Long userId);

    PatientPaymentAccount findByAccountId(Long aLong);
}
