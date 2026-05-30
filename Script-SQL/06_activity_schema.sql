-- =====================================================
-- EVENTRA - MICROSERVICIO ACTIVITY
-- ESQUEMA DE BASE DE DATOS
-- PostgreSQL
-- Ejecutar dentro de: eventra_activity
-- =====================================================


-- =====================================================
-- TABLA: activity_sessions
-- Guarda sesiones activas y finalizadas de actividades deportivas
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


-- =====================================================
-- TABLA: activity_locations
-- Guarda ubicaciones GPS de cada actividad
-- =====================================================

CREATE TABLE activity_locations (
    id BIGSERIAL PRIMARY KEY,

    activity_session_id BIGINT NOT NULL,

    latitude NUMERIC(10,7) NOT NULL,
    longitude NUMERIC(10,7) NOT NULL,

    altitude NUMERIC(8,2),
    speed_kmh NUMERIC(8,2),

    accuracy_meters NUMERIC(8,2),

    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_locations_session
        FOREIGN KEY (activity_session_id)
        REFERENCES activity_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_latitude
        CHECK (
            latitude >= -90
            AND latitude <= 90
        ),

    CONSTRAINT chk_longitude
        CHECK (
            longitude >= -180
            AND longitude <= 180
        ),

    CONSTRAINT chk_speed
        CHECK (
            speed_kmh IS NULL
            OR speed_kmh >= 0
        ),

    CONSTRAINT chk_accuracy
        CHECK (
            accuracy_meters IS NULL
            OR accuracy_meters >= 0
        )
);

CREATE INDEX idx_activity_locations_session_id
    ON activity_locations(activity_session_id);

CREATE INDEX idx_activity_locations_recorded_at
    ON activity_locations(recorded_at);

CREATE INDEX idx_activity_locations_session_recorded
    ON activity_locations(activity_session_id, recorded_at);