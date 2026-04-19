-- =====================================================
-- EVENTRA - MICROSERVICIO REGISTRATIONS
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_registrations
-- =====================================================

CREATE TABLE event_registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    auth_user_id BIGINT NOT NULL,
    registration_status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_event_registration_unique
        UNIQUE (event_id, auth_user_id),

    CONSTRAINT chk_registration_status
        CHECK (registration_status IN ('REGISTERED', 'CANCELLED', 'ATTENDED'))
);

CREATE INDEX idx_event_registrations_event_id
    ON event_registrations(event_id);

CREATE INDEX idx_event_registrations_auth_user_id
    ON event_registrations(auth_user_id);

CREATE INDEX idx_event_registrations_status
    ON event_registrations(registration_status);