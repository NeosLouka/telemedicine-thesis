package com.biopix.telemedicine_backend.doctor.internal.entity;

import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 30)
    private String phone;

    @Column(length = 20)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String country;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "DOCTOR";

    @Column(name = "availability_status", nullable = false, length = 30)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private DoctorAvailabilityStatus availabilityStatus = DoctorAvailabilityStatus.OFFLINE;

    @Column(name = "license_number", nullable = false, unique = true, length = 100)
    private String licenseNumber;

    @Column(name = "clinic_address", columnDefinition = "TEXT")
    private String clinicAddress;

    @Column(name = "clinic_phone", length = 30)
    private String clinicPhone;

    // Mapping the relationship to the Speciality entity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "speciality_id", nullable = false)
    private Speciality speciality;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
