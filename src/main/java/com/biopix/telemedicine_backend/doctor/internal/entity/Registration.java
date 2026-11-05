package com.biopix.telemedicine_backend.doctor.internal.entity;

import com.biopix.telemedicine_backend.doctor.internal.enums.ReviewResult;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctor_registrations")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Inside the same domain module, direct JPA relations are perfectly fine
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false, unique = true)
    private Doctor doctor;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "reviewed_by_admin_id")
    private Long reviewedByAdminId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "review_result", length = 50)
    private ReviewResult reviewResult = ReviewResult.PENDING;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
