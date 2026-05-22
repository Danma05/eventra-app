-- =====================================================
-- EVENTRA - MICROSERVICIO RESULTS
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_results
-- =====================================================

CREATE TABLE event_results (
    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,
    auth_user_id BIGINT NOT NULL,

    position INT NOT NULL,
    bib_number VARCHAR(20),

    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',

    total_time_seconds INT NOT NULL,
    pace_seconds_per_km INT,
    distance_km NUMERIC(8,2),

    result_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_event_result_user
        UNIQUE (event_id, auth_user_id),

    CONSTRAINT uq_event_result_position
        UNIQUE (event_id, position),

    CONSTRAINT chk_result_status
        CHECK (result_status IN ('DRAFT', 'PUBLISHED', 'CANCELLED')),

    CONSTRAINT chk_result_position
        CHECK (position > 0),

    CONSTRAINT chk_total_time
        CHECK (total_time_seconds > 0),

    CONSTRAINT chk_pace
        CHECK (pace_seconds_per_km IS NULL OR pace_seconds_per_km > 0),

    CONSTRAINT chk_distance
        CHECK (distance_km IS NULL OR distance_km > 0)
);

CREATE INDEX idx_event_results_event_id
    ON event_results(event_id);

CREATE INDEX idx_event_results_auth_user_id
    ON event_results(auth_user_id);

CREATE INDEX idx_event_results_status
    ON event_results(result_status);

CREATE INDEX idx_event_results_position
    ON event_results(event_id, position);