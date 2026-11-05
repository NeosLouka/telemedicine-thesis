
CREATE TABLE specialities (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR(100) NOT NULL UNIQUE,
                              description TEXT
);

CREATE TABLE doctors
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
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    role            VARCHAR(20) NOT NULL DEFAULT 'DOCTOR',

    availability_status VARCHAR(30) NOT NULL DEFAULT 'OFFLINE',
    speciality_id BIGINT NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    clinic_address TEXT,
    clinic_phone VARCHAR(30),

    CONSTRAINT fk_doctor_speciality
        FOREIGN KEY (speciality_id)
            REFERENCES specialities(id),


    CONSTRAINT chk_doctor_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER') OR gender IS NULL),


    CONSTRAINT chk_doctor_availability_status
        CHECK (availability_status IN ('ONLINE', 'BUSY', 'OFFLINE'))



);






CREATE TABLE doctor_registrations (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL UNIQUE,

    reviewed_at TIMESTAMPTZ,

    reviewed_by_admin_id BIGINT,
    review_result VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    review_comment TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_doctor_registration_doctor
      FOREIGN KEY (doctor_id)
          REFERENCES doctors(id),

    CONSTRAINT chk_doctor_registration_review_result
      CHECK (
          review_result IN (
                            'PENDING',
                            'INFO_REQUESTED',
                            'APPROVED_BY_ADMIN',
                            'REJECTED_BY_ADMIN'
              )
              OR review_result IS NULL
          ),

    CONSTRAINT chk_review_requires_admin
      CHECK (
          (review_result = 'PENDING' AND reviewed_by_admin_id IS NULL AND reviewed_at IS NULL)
              OR
          (review_result <> 'PENDING' AND reviewed_by_admin_id IS NOT NULL AND reviewed_at IS NOT NULL)
          )
);

CREATE TABLE doctor_registration_documents (
    id BIGSERIAL PRIMARY KEY,

    registration_id BIGINT NOT NULL,

    doc_type VARCHAR(50) NOT NULL,
    file_key TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    expiry_date DATE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_registration_document_registration
       FOREIGN KEY (registration_id)
           REFERENCES doctor_registrations(id),

    CONSTRAINT chk_registration_document_type
       CHECK (
           doc_type IN (
                        'MEDICAL_LICENSE',
                        'MEDICAL_DEGREE',
                        'SPECIALITY_TITLE',
                        'MEMBERSHIP_CERT'
               )
           ),

    CONSTRAINT chk_registration_document_status
       CHECK (
           status IN (
                      'PENDING',
                      'UPLOADED',
                      'APPROVED',
                      'REJECTED'
               )
           ),

    CONSTRAINT chk_uploaded_document_has_file
       CHECK (
           status = 'PENDING'
               OR file_key IS NOT NULL
           ),
    
    CONSTRAINT uq_registration_document_type
       UNIQUE (registration_id, doc_type)
);



INSERT INTO specialities (name, description)
VALUES
    ('Pediatrician', 'Medical care for children'),
    ('General Practitioner', 'General medical advice'),
    ('Dermatologist', 'Skin-related conditions'),
    ('Gynecologist', 'Medical care related to female reproductive health'),
    ('Urologist', 'Urinary and male reproductive health issues'),
    ('Venereologist', 'Sexually transmitted infections')
ON CONFLICT (name) DO NOTHING;