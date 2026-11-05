CREATE TABLE user_fcm_tokens (
     id BIGSERIAL PRIMARY KEY,
     account_id BIGINT NOT NULL,
     fcm_token VARCHAR(500) NOT NULL,
     device_type VARCHAR(32),
     created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Ensures a physical FCM token cannot belong to multiple rows simultaneously
     CONSTRAINT uk_user_fcm_tokens_token UNIQUE (fcm_token)
);

CREATE INDEX idx_user_fcm_tokens_user_id ON user_fcm_tokens(account_id);