package com.biopix.telemedicine_backend.video.internal.twilio.entity;



import com.biopix.telemedicine_backend.video.internal.twilio.enums.VideoSessionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;


@Entity
@Table(
        name = "video_session",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_video_session_consultation",
                        columnNames = "consultation_id"
                ),
                @UniqueConstraint(
                        name = "uq_video_session_room_name",
                        columnNames = "room_name"
                ),
                @UniqueConstraint(
                        name = "uq_video_session_external_room_id",
                        columnNames = "external_room_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSession {


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

    @Builder.Default
    @Column(
            name = "patient_joined",
            nullable = false
    )
    private Boolean patientJoined = Boolean.FALSE;


    @Column(
            name = "doctor_account_id",
            nullable = false
    )
    private Long doctorAccountId;

    @Builder.Default
    @Column(
            name = "doctor_joined",
            nullable = false
    )
    private Boolean doctorJoined = Boolean.FALSE;

    @Column(
            name = "scheduled_start_time",
            nullable = false
    )
    private Instant scheduledStartTime;

    /**
     * Internal room name.
     * Example:
     * consultation-123
     */
    @Column(
            name = "room_name",
            nullable = false,
            unique = true
    )
    private String roomName;


    /**
     * External provider room identifier.
     * For Twilio:
     * RMxxxxxxxxxxxxxxxx
     */
    @Column(
            name = "external_room_id",
            nullable = false,
            unique = true
    )
    private String externalRoomId;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private VideoSessionStatus status = VideoSessionStatus.CREATED;


    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;


    @Column(name = "ended_at")
    private Instant endedAt;
}