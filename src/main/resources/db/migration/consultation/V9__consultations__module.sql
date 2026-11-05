CREATE TABLE consultations (
    id BIGSERIAL PRIMARY KEY,

    patient_request_id BIGINT NOT NULL,
    doctor_offer_id BIGINT NOT NULL,

    patient_account_id BIGINT NOT NULL,
    doctor_account_id BIGINT NOT NULL,

    patient_email VARCHAR(255) NOT NULL,
    doctor_email VARCHAR(255) NOT NULL,

    price NUMERIC(10, 2) NOT NULL,
    request_type VARCHAR(10) NOT NULL,

    scheduled_start_time TIMESTAMPTZ NOT NULL,
    estimated_duration_minutes INTEGER NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    ready_at TIMESTAMPTZ,

    cancelled_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,

    no_show_marked_at TIMESTAMPTZ,
    no_show_reason VARCHAR(50),

    failure_code VARCHAR(50),
    failure_reason VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_consultation_request
       UNIQUE (patient_request_id),

    CONSTRAINT uq_consultation_offer
       UNIQUE (doctor_offer_id),

    CONSTRAINT chk_consultation_participants
       CHECK (patient_account_id <> doctor_account_id),

    CONSTRAINT chk_consultation_price
       CHECK (price >= 0),

    CONSTRAINT chk_consultation_duration
       CHECK (estimated_duration_minutes > 0),

    CONSTRAINT chk_consultation_status
       CHECK (
           status IN (
                      'PENDING',
                      'READY',
                      'IN_PROGRESS',
                      'AWAITING_SUMMARY',
                      'COMPLETED',
                      'CANCELLED',
                      'NO_SHOW',
                      'FAILED'
               )
           ),

    CONSTRAINT chk_consultation_no_show_reason
       CHECK (
           no_show_reason IS NULL
               OR no_show_reason IN (
                                     'PATIENT_DID_NOT_JOIN',
                                     'DOCTOR_DID_NOT_JOIN',
                                     'BOTH_DID_NOT_JOIN'
               )
           ),

    CONSTRAINT chk_consultation_failure_fields
       CHECK (
           status = 'FAILED'
               OR (
               failure_code IS NULL
                   AND failure_reason IS NULL
               )
           ),

    CONSTRAINT chk_consultation_request_type
        CHECK (request_type IN ('NOW', 'SOON')),

    CONSTRAINT chk_consultation_ready_false
        CHECK (
            (status = 'PENDING' AND ready_at IS NULL  )
                OR
            (status <> 'PENDING' AND ready_at IS NOT NULL)
            ),

    CONSTRAINT chk_consultation_cancelled_fields
        CHECK (
            (status = 'CANCELLED' AND cancelled_at IS NOT NULL)
                OR
            (status <> 'CANCELLED' AND cancelled_at IS NULL)
            ),
    CONSTRAINT chk_consultation_completed_fields
        CHECK (
            (status IN ('AWAITING_SUMMARY', 'COMPLETED') AND completed_at IS NOT NULL)
                OR
            (status NOT IN ('AWAITING_SUMMARY', 'COMPLETED') AND completed_at IS NULL)
            ),
    CONSTRAINT chk_consultation_no_show_fields
        CHECK (
            (status = 'NO_SHOW' AND no_show_marked_at IS NOT NULL AND no_show_reason IS NOT NULL)
                OR
            (status <> 'NO_SHOW' AND no_show_marked_at IS NULL AND no_show_reason IS NULL)
            )

);