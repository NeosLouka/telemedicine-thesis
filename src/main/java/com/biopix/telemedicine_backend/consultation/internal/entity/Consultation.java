package com.biopix.telemedicine_backend.consultation.internal.entity;

import com.biopix.telemedicine_backend.consultation.internal.enums.ConsultationStatus;
import com.biopix.telemedicine_backend.consultation.internal.enums.NoShowReason;
import com.biopix.telemedicine_backend.consultation.internal.enums.RequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "consultations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_consultation_request", columnNames = "patient_request_id"),
                @UniqueConstraint(name = "uq_consultation_offer", columnNames = "doctor_offer_id")
        }
)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "patient_request_id", nullable = false)
    private Long patientRequestId;

    @NotNull
    @Column(name = "doctor_offer_id", nullable = false)
    private Long doctorOfferId;

    @NotNull
    @Column(name = "patient_account_id", nullable = false)
    private Long patientAccountId;

    @NotNull
    @Column(name = "doctor_account_id", nullable = false)
    private Long doctorAccountId;

    @NotNull
    @Column(name = "patient_email", nullable = false)
    private String patientEmail;

    @NotNull
    @Column(name = "doctor_email", nullable = false)
    private String doctorEmail;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 10)
    private RequestType requestType;

    @NotNull
    @Column(name = "scheduled_start_time", nullable = false)
    private Instant scheduledStartTime;

    @NotNull
    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDurationMinutes;

    @Builder.Default
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConsultationStatus status = ConsultationStatus.PENDING;

    @Column(name = "ready_at")
    private Instant readyAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "no_show_marked_at")
    private Instant noShowMarkedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "no_show_reason", length = 50)
    private NoShowReason noShowReason;

    @Column(name = "failure_code", length = 50)
    private String failureCode;

    @Column(name = "failure_reason")
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
