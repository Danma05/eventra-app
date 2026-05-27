-- =====================================================
-- EVENTRA - MICROSERVICIO ACTIVITY
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_activity
-- =====================================================

CREATE TABLE activity_sessions (
    id BIGSERIAL PRIMARY KEY,

    event_id BIGINT NOT NULL,
    auth_user_id BIGINT NOT NULL,

    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,

    total_time_seconds INT NOT NULL DEFAULT 0,
    total_distance_km NUMERIC(10,3) NOT NULL DEFAULT 0,
    average_speed_kmh NUMERIC(8,2) NOT NULL DEFAULT 0,
    average_pace_seconds_per_km INT,
    calories_burned INT NOT NULL DEFAULT 0,

    activity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_activity_status
        CHECK (activity_status IN ('ACTIVE', 'FINISHED', 'CANCELLED')),

    CONSTRAINT chk_activity_time
        CHECK (total_time_seconds >= 0),

    CONSTRAINT chk_activity_distance
        CHECK (total_distance_km >= 0),

    CONSTRAINT chk_activity_speed
        CHECK (average_speed_kmh >= 0),

    CONSTRAINT chk_activity_pace
        CHECK (
            average_pace_seconds_per_km IS NULL
            OR average_pace_seconds_per_km > 0
        ),

    CONSTRAINT chk_activity_calories
        CHECK (calories_burned >= 0)
);

CREATE INDEX idx_activity_sessions_event_id
    ON activity_sessions(event_id);

CREATE INDEX idx_activity_sessions_auth_user_id
    ON activity_sessions(auth_user_id);

CREATE INDEX idx_activity_sessions_status
    ON activity_sessions(activity_status);

CREATE INDEX idx_activity_sessions_event_status
    ON activity_sessions(event_id, activity_status);

CREATE UNIQUE INDEX uq_active_activity_by_user
    ON activity_sessions(auth_user_id)
    WHERE activity_status = 'ACTIVE';