package com.biopix.telemedicine_backend.marketplace.internal.entity;

import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consultation_requests", indexes = {
        @Index(name = "uq_open_request_per_patient", columnList = "patient_email", unique = true)
})
@Entity
public class ConsultationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_account_id", nullable = false)
    private Long patientAccountId;

    @Email
    @Column(name = "patient_email", length = 255, nullable = false)
    private String patientEmail;

    @Column(name = "speciality_id", nullable = false)
    private Long specialityId;

    @Column(name = "issue_description", nullable = false, columnDefinition = "TEXT")
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private ConsultationRequestType requestType;

    @Column(name = "preferred_start_time")
    private Instant preferredStartTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConsultationRequestStatus status = ConsultationRequestStatus.OPEN;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
