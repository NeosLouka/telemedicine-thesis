
CREATE TABLE doctor_payment_account (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    external_account_id VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE patient_payment_account (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    external_account_id VARCHAR(255) NOT NULL UNIQUE
);


CREATE TABLE consultation_payment (
    id BIGSERIAL PRIMARY KEY,

    consultation_id BIGINT NOT NULL UNIQUE,

    patient_account_id BIGINT NOT NULL,
    doctor_account_id BIGINT NOT NULL,

    patient_payment_account_id BIGINT NOT NULL,
    doctor_payment_account_id BIGINT NOT NULL,

    price NUMERIC(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    status VARCHAR(30) NOT NULL,

    external_payment_id VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_consultation_payment_patient_account
      FOREIGN KEY (patient_payment_account_id)
          REFERENCES patient_payment_account(id),

    CONSTRAINT fk_consultation_payment_doctor_account
      FOREIGN KEY (doctor_payment_account_id)
          REFERENCES doctor_payment_account(id)
);