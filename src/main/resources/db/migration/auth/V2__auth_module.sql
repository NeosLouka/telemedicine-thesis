CREATE TABLE auth_accounts
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(30)  NOT NULL,
    account_status VARCHAR(40) NOT NULL DEFAULT 'PENDING_PROFILE_COMPLETION',
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,


    CONSTRAINT chk_auth_account_role
        CHECK (role IN ('PATIENT', 'DOCTOR', 'ADMIN')),

    CONSTRAINT chk_auth_account_status
        CHECK (
            account_status IN (
                    'PENDING_PROFILE_COMPLETION',
                    'PENDING_ADMIN_REVIEW',
                    'ACTIVE',
                    'REJECTED',
                    'SUSPENDED'
                )
            )

);
