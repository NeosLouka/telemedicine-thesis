CREATE TABLE video_session (
    id BIGSERIAL PRIMARY KEY,

    consultation_id BIGINT NOT NULL UNIQUE,

    patient_account_id BIGINT NOT NULL,
    patient_joined BOOLEAN NOT NULL DEFAULT FALSE,

    doctor_account_id BIGINT NOT NULL,
    doctor_joined BOOLEAN NOT NULL DEFAULT FALSE,

    scheduled_start_time TIMESTAMPTZ NOT NULL,

    room_name VARCHAR(255) NOT NULL UNIQUE,
    external_room_id VARCHAR(255) NOT NULL UNIQUE,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMPTZ
);