package com.biopix.telemedicine_backend.payment.internal.repo;

import com.biopix.telemedicine_backend.payment.internal.entity.DoctorPaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorPaymentAccountRepository extends JpaRepository<DoctorPaymentAccount, Long> {
    boolean existsByAccountId(Long userId);

    DoctorPaymentAccount findByAccountId(Long aLong);
}
