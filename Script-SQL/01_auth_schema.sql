-- =====================================================
-- EVENTRA - MICROSERVICIO AUTH
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_auth
-- =====================================================

-- =====================================================
-- TABLA: users_auth
-- Guarda credenciales y estado de autenticación
-- =====================================================
CREATE TABLE users_auth (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    account_type VARCHAR(20) NOT NULL DEFAULT 'RUNNER',
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_auth_status
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'BLOCKED')),

    CONSTRAINT chk_users_auth_account_type
        CHECK (account_type IN ('RUNNER', 'ORGANIZER')),

    CONSTRAINT chk_failed_login_attempts
        CHECK (failed_login_attempts >= 0)
);

-- =====================================================
-- TABLA: user_sessions
-- Guarda sesiones emitidas para los usuarios
-- =====================================================
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_auth_id BIGINT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_sessions_user_auth
        FOREIGN KEY (user_auth_id)
        REFERENCES users_auth(id)
        ON DELETE CASCADE
);

-- =====================================================
-- TABLA: password_reset_tokens
-- Guarda tokens para recuperación de contraseña
-- =====================================================
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_auth_id BIGINT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_password_reset_user_auth
        FOREIGN KEY (user_auth_id)
        REFERENCES users_auth(id)
        ON DELETE CASCADE
);

-- =====================================================
-- TABLA: email_verification_tokens
-- Guarda tokens para verificación de correo electrónico
-- =====================================================
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_auth_id BIGINT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_email_verification_user_auth
        FOREIGN KEY (user_auth_id)
        REFERENCES users_auth(id)
        ON DELETE CASCADE
);

-- =====================================================
-- ÍNDICES
-- =====================================================

CREATE INDEX idx_users_auth_email
    ON users_auth(email);

CREATE INDEX idx_user_sessions_user_auth_id
    ON user_sessions(user_auth_id);

CREATE INDEX idx_user_sessions_expires_at
    ON user_sessions(expires_at);

CREATE INDEX idx_password_reset_user_auth_id
    ON password_reset_tokens(user_auth_id);

CREATE INDEX idx_password_reset_expires_at
    ON password_reset_tokens(expires_at);

CREATE INDEX idx_email_verification_user_auth_id
    ON email_verification_tokens(user_auth_id);

CREATE INDEX idx_email_verification_expires_at
    ON email_verification_tokens(expires_at);