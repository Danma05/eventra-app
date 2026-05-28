const pool = require("../config/db");

const findActiveSessionByUser = async (authUserId) => {
  const result = await pool.query(
    `SELECT *
     FROM activity_sessions
     WHERE auth_user_id = $1
       AND activity_status = 'ACTIVE'`,
    [authUserId]
  );

  return result.rows[0];
};

const createActivitySession = async (eventId, authUserId) => {
  const result = await pool.query(
    `INSERT INTO activity_sessions
     (event_id, auth_user_id)
     VALUES ($1, $2)
     RETURNING *`,
    [eventId, authUserId]
  );

  return result.rows[0];
};

const findSessionById = async (sessionId) => {
  const result = await pool.query(
    `SELECT *
     FROM activity_sessions
     WHERE id = $1`,
    [sessionId]
  );

  return result.rows[0];
};

const saveActivityLocation = async ({
  activity_session_id,
  latitude,
  longitude,
  altitude,
  speed_kmh,
  accuracy_meters,
}) => {
  const result = await pool.query(
    `INSERT INTO activity_locations
     (
       activity_session_id,
       latitude,
       longitude,
       altitude,
       speed_kmh,
       accuracy_meters
     )
     VALUES ($1, $2, $3, $4, $5, $6)
     RETURNING *`,
    [
      activity_session_id,
      latitude,
      longitude,
      altitude || null,
      speed_kmh || 0,
      accuracy_meters || null,
    ]
  );

  return result.rows[0];
};

const finishActivitySession = async ({
  sessionId,
  total_time_seconds,
  total_distance_km,
  average_speed_kmh,
  average_pace_seconds_per_km,
  calories_burned,
}) => {
  const result = await pool.query(
    `UPDATE activity_sessions
     SET
       finished_at = CURRENT_TIMESTAMP,
       total_time_seconds = $1,
       total_distance_km = $2,
       average_speed_kmh = $3,
       average_pace_seconds_per_km = $4,
       calories_burned = $5,
       activity_status = 'FINISHED',
       updated_at = CURRENT_TIMESTAMP
     WHERE id = $6
       AND activity_status = 'ACTIVE'
     RETURNING *`,
    [
      total_time_seconds,
      total_distance_km,
      average_speed_kmh,
      average_pace_seconds_per_km || null,
      calories_burned || 0,
      sessionId,
    ]
  );

  return result.rows[0];
};

const getActiveParticipantsByEvent = async (eventId) => {
  const result = await pool.query(
    `SELECT DISTINCT ON (s.auth_user_id)
        s.id AS activity_session_id,
        s.event_id,
        s.auth_user_id,
        s.started_at,
        s.total_time_seconds,
        s.total_distance_km,
        s.average_speed_kmh,
        l.latitude,
        l.longitude,
        l.altitude,
        l.speed_kmh,
        l.accuracy_meters,
        l.recorded_at,
        RANK() OVER (
          ORDER BY s.total_distance_km DESC, s.total_time_seconds ASC
        ) AS current_position
     FROM activity_sessions s
     LEFT JOIN activity_locations l
        ON l.activity_session_id = s.id
     WHERE s.event_id = $1
       AND s.activity_status = 'ACTIVE'
     ORDER BY s.auth_user_id, l.recorded_at DESC`,
    [eventId]
  );

  return result.rows;
};

module.exports = {
  findActiveSessionByUser,
  createActivitySession,
  findSessionById,
  saveActivityLocation,
  finishActivitySession,
  getActiveParticipantsByEvent,
};