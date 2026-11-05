package com.biopix.telemedicine_backend.payment.internal.repo;

import com.biopix.telemedicine_backend.payment.internal.entity.ConsultationPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationPaymentRepository extends JpaRepository<ConsultationPayment, String> {
}
