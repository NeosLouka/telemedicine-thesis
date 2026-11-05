

CREATE TABLE consultation_requests (
    id BIGSERIAL PRIMARY KEY,

    patient_account_id BIGINT NOT NULL,
    patient_email VARCHAR(255) NOT NULL,

    speciality_id BIGINT NOT NULL,

    issue_description TEXT NOT NULL,

    request_type VARCHAR(20) NOT NULL,
    preferred_start_time TIMESTAMPTZ,

    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',

    expires_at TIMESTAMPTZ,
    fulfilled_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_consultation_request_type
       CHECK (
           request_type IN (
                            'NOW',
                            'SOON'
               )
           ),

    CONSTRAINT chk_consultation_request_status
       CHECK (
           status IN (
                      'OPEN',
                      'FULFILLED',
                      'EXPIRED',
                      'CANCELLED'
               )
           ),

    CONSTRAINT chk_consultation_request_time_logic
       CHECK (
           (request_type = 'NOW' AND preferred_start_time IS NULL)
               OR
           (request_type = 'SOON' AND preferred_start_time IS NOT NULL)
           )


);



CREATE TABLE consultation_offers (
    id BIGSERIAL PRIMARY KEY,

    request_id BIGINT NOT NULL,

    doctor_account_id BIGINT NOT NULL ,
    doctor_email VARCHAR(255) NOT NULL,

    price NUMERIC(10, 2) NOT NULL,
    proposed_start_time TIMESTAMPTZ NOT NULL,
    estimated_duration_minutes INT,

    message TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    accepted_at TIMESTAMPTZ,
    rejected_at TIMESTAMPTZ,
    expired_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_doctor_offer_request
       FOREIGN KEY (request_id)
           REFERENCES consultation_requests(id)
           ON DELETE CASCADE,

    CONSTRAINT uq_doctor_offer_request_doctor
       UNIQUE (request_id, doctor_account_id),


    CONSTRAINT chk_doctor_offer_status
       CHECK (
           status IN (
                      'PENDING',
                      'ACCEPTED',
                      'REJECTED',
                      'EXPIRED'
               )
           ),

    CONSTRAINT chk_doctor_offer_price_positive
       CHECK (price > 0),

    CONSTRAINT chk_doctor_offer_duration_positive
       CHECK (
           estimated_duration_minutes IS NULL
               OR estimated_duration_minutes > 0
           )
);


CREATE UNIQUE INDEX uq_open_request_per_patient
    ON consultation_requests (patient_account_id)
    WHERE status = 'OPEN';





