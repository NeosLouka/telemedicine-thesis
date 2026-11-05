package com.biopix.telemedicine_backend.payment.internal.entity;


import com.biopix.telemedicine_backend.payment.internal.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "consultation_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "consultation_id",
            nullable = false,
            unique = true
    )
    private Long consultationId;

    @Column(
            name = "patient_account_id",
            nullable = false
    )
    private Long patientAccountId;

    @Column(
            name = "doctor_account_id",
            nullable = false
    )
    private Long doctorAccountId;

    @Column(
            name = "patient_payment_account_id",
            nullable = false
    )
    private Long patientPaymentAccountId;

    @Column(
            name = "doctor_payment_account_id",
            nullable = false
    )
    private Long doctorPaymentAccountId;

    @Column(
            name = "price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    @Builder.Default
    @Column(
            name = "currency",
            nullable = false,
            length = 3
    )
    private String currency = "EUR";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private PaymentStatus status =  PaymentStatus.PENDING;

    @Column(
            name = "external_payment_id"
    )
    private String externalPaymentId;

    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
}