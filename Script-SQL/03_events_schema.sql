-- =====================================================
-- EVENTRA - MICROSERVICIO EVENTS
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_events
-- =====================================================

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    organizer_auth_user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL DEFAULT 1,
    image_url TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_events_capacity
        CHECK (capacity > 0),

    CONSTRAINT chk_events_status
        CHECK (status IN ('ACTIVE', 'CANCELLED', 'FINISHED'))
);

CREATE INDEX idx_events_organizer_auth_user_id
    ON events(organizer_auth_user_id);

CREATE INDEX idx_events_event_date
    ON events(event_date);

CREATE INDEX idx_events_status
    ON events(status);