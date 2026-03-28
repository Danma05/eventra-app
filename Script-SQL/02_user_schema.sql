-- =====================================================
-- EVENTRA - MICROSERVICIO USER
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_user_db
-- =====================================================

-- =====================================================
-- TABLA: user_profile
-- Guarda la información principal del perfil del usuario
-- =====================================================
CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    auth_user_id BIGINT NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    birth_date DATE,
    gender VARCHAR(25) NOT NULL DEFAULT 'PREFER_NOT_TO_SAY',
    profile_image_url TEXT,
    bio TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    profile_status VARCHAR(20) NOT NULL DEFAULT 'INCOMPLETE',
    is_public_profile BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_user_profile_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),

    CONSTRAINT chk_user_profile_status
        CHECK (profile_status IN ('ACTIVE', 'INCOMPLETE', 'SUSPENDED'))
);

-- =====================================================
-- TABLA: user_preferences
-- Guarda preferencias de uso de la aplicación
-- =====================================================
CREATE TABLE user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL UNIQUE,
    preferred_language VARCHAR(10) NOT NULL DEFAULT 'es',
    notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    dark_mode_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    location_sharing_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    marketing_emails_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_preferences_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile(id)
        ON DELETE CASCADE
);

-- =====================================================
-- TABLA: user_emergency_contacts
-- Guarda contactos de emergencia del usuario
-- =====================================================
CREATE TABLE user_emergency_contacts (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL,
    contact_name VARCHAR(150) NOT NULL,
    relationship VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_emergency_contacts_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile(id)
        ON DELETE CASCADE
);

-- =====================================================
-- TABLA: user_physical_info
-- Guarda información física básica del usuario
-- =====================================================
CREATE TABLE user_physical_info (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL UNIQUE,
    height_cm NUMERIC(5,2),
    weight_kg NUMERIC(5,2),
    blood_type VARCHAR(5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_physical_info_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_physical_info_height
        CHECK (height_cm IS NULL OR height_cm > 0),

    CONSTRAINT chk_user_physical_info_weight
        CHECK (weight_kg IS NULL OR weight_kg > 0),

    CONSTRAINT chk_user_physical_info_blood_type
        CHECK (
            blood_type IS NULL OR
            blood_type IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')
        )
);

-- =====================================================
-- TABLA: user_medical_info
-- Guarda información médica básica relevante para emergencias
-- =====================================================
CREATE TABLE user_medical_info (
    id BIGSERIAL PRIMARY KEY,
    user_profile_id BIGINT NOT NULL UNIQUE,
    has_allergies BOOLEAN NOT NULL DEFAULT FALSE,
    allergies_description TEXT,
    has_medical_conditions BOOLEAN NOT NULL DEFAULT FALSE,
    medical_conditions_description TEXT,
    takes_medication BOOLEAN NOT NULL DEFAULT FALSE,
    medication_description TEXT,
    emergency_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_medical_info_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile(id)
        ON DELETE CASCADE
);

-- =====================================================
-- ÍNDICES
-- =====================================================

CREATE INDEX idx_user_profile_auth_user_id
    ON user_profile(auth_user_id);

CREATE INDEX idx_user_profile_username
    ON user_profile(username);

CREATE INDEX idx_user_preferences_user_profile_id
    ON user_preferences(user_profile_id);

CREATE INDEX idx_user_emergency_contacts_user_profile_id
    ON user_emergency_contacts(user_profile_id);

CREATE INDEX idx_user_physical_info_user_profile_id
    ON user_physical_info(user_profile_id);

CREATE INDEX idx_user_medical_info_user_profile_id
    ON user_medical_info(user_profile_id);