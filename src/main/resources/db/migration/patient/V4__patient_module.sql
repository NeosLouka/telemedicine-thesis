
CREATE TABLE patients
(
    id             BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth  DATE,
    phone          VARCHAR(30),
    gender         VARCHAR(20),
    address        TEXT,
    country        VARCHAR(100),
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    role            VARCHAR(20) NOT NULL DEFAULT 'PATIENT',


    CONSTRAINT chk_patient_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER') OR gender IS NULL)


);
