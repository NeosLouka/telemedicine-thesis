package com.biopix.telemedicine_backend.payment.internal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_payment_account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientPaymentAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "account_id",
            nullable = false,
            unique = true
    )
    private Long accountId;

    @Column(
            name = "external_account_id",
            nullable = false,
            unique = true
    )
    private String externalAccountId;
}