CREATE TABLE IF NOT EXISTS race_tracking (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,

    current_latitude DECIMAL(10,8),
    current_longitude DECIMAL(11,8),

    distance_completed_km DECIMAL(10,2) DEFAULT 0,
    distance_to_finish_meters DECIMAL(10,2) DEFAULT 0,
    gap_to_previous_meters DECIMAL(10,2) DEFAULT 0,

    current_position INTEGER,
    status VARCHAR(20) DEFAULT 'REGISTERED',

    started_at TIMESTAMP,
    paused_at TIMESTAMP,
    resumed_at TIMESTAMP,
    finished_at TIMESTAMP,
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tracking_status
    CHECK (status IN ('REGISTERED', 'RUNNING', 'PAUSED', 'FINISHED', 'DNF'))
);

CREATE INDEX IF NOT EXISTS idx_race_tracking_event_id
ON race_tracking(event_id);

CREATE INDEX IF NOT EXISTS idx_race_tracking_user_id
ON race_tracking(user_id);

CREATE INDEX IF NOT EXISTS idx_race_tracking_status
ON race_tracking(status);