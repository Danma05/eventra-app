const pool = require("../config/db");

const findOpenSessionByUser = async (authUserId) => {
  const result = await pool.query(
    `SELECT *
     FROM activity_sessions
     WHERE auth_user_id = $1
       AND activity_status IN ('ACTIVE', 'PAUSED')`,
    [authUserId]
  );
  return result.rows[0];
};

const createActivitySession = async (eventId, authUserId) => {
  const result = await pool.query(
    `INSERT INTO activity_sessions (event_id, auth_user_id, activity_status)
     VALUES ($1, $2, 'ACTIVE')
     RETURNING *`,
    [eventId, authUserId]
  );
  return result.rows[0];
};

const findSessionById = async (sessionId) => {
  const result = await pool.query(`SELECT * FROM activity_sessions WHERE id = $1`, [sessionId]);
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
     (activity_session_id, latitude, longitude, altitude, speed_kmh, accuracy_meters)
     VALUES ($1, $2, $3, $4, $5, $6)
     RETURNING *`,
    [activity_session_id, latitude, longitude, altitude || null, speed_kmh || 0, accuracy_meters || null]
  );
  return result.rows[0];
};

const updateLiveSessionStats = async ({
  sessionId,
  latitude,
  longitude,
  distanceToFinishMeters,
  total_time_seconds,
  total_distance_km,
  average_speed_kmh,
}) => {
  const result = await pool.query(
    `UPDATE activity_sessions
     SET last_latitude = $1,
         last_longitude = $2,
         distance_to_finish_meters = $3,
         total_time_seconds = COALESCE($4, total_time_seconds),
         total_distance_km = COALESCE($5, total_distance_km),
         average_speed_kmh = COALESCE($6, average_speed_kmh),
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $7
     RETURNING *`,
    [latitude, longitude, distanceToFinishMeters, total_time_seconds ?? null, total_distance_km ?? null, average_speed_kmh ?? null, sessionId]
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
     SET finished_at = CURRENT_TIMESTAMP,
         total_time_seconds = COALESCE($1, total_time_seconds),
         total_distance_km = COALESCE($2, total_distance_km),
         average_speed_kmh = COALESCE($3, average_speed_kmh),
         average_pace_seconds_per_km = COALESCE($4, average_pace_seconds_per_km),
         calories_burned = COALESCE($5, calories_burned),
         activity_status = 'FINISHED',
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $6
       AND activity_status IN ('ACTIVE', 'PAUSED')
     RETURNING *`,
    [total_time_seconds ?? null, total_distance_km ?? null, average_speed_kmh ?? null, average_pace_seconds_per_km ?? null, calories_burned ?? null, sessionId]
  );
  return result.rows[0];
};

const pauseActivitySession = async (sessionId) => {
  const result = await pool.query(
    `UPDATE activity_sessions
     SET activity_status = 'PAUSED',
         paused_at = CURRENT_TIMESTAMP,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $1
       AND activity_status = 'ACTIVE'
     RETURNING *`,
    [sessionId]
  );
  return result.rows[0];
};

const resumeActivitySession = async (sessionId) => {
  const result = await pool.query(
    `UPDATE activity_sessions
     SET activity_status = 'ACTIVE',
         resumed_at = CURRENT_TIMESTAMP,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $1
       AND activity_status = 'PAUSED'
     RETURNING *`,
    [sessionId]
  );
  return result.rows[0];
};

const markActiveSessionsAsDnfByEvent = async (eventId) => {
  const result = await pool.query(
    `UPDATE activity_sessions
     SET activity_status = 'DNF',
         finished_at = CURRENT_TIMESTAMP,
         updated_at = CURRENT_TIMESTAMP
     WHERE event_id = $1
       AND activity_status IN ('ACTIVE', 'PAUSED')
     RETURNING *`,
    [eventId]
  );
  return result.rows;
};

const getLiveRankingByEvent = async (eventId) => {
  const result = await pool.query(
    `WITH ranked AS (
       SELECT
         s.id AS activity_session_id,
         s.event_id,
         s.auth_user_id,
         s.started_at,
         s.finished_at,
         s.total_time_seconds,
         s.total_distance_km,
         s.average_speed_kmh,
         s.last_latitude AS latitude,
         s.last_longitude AS longitude,
         s.distance_to_finish_meters,
         s.activity_status,
         ROW_NUMBER() OVER (
           ORDER BY
             CASE WHEN s.activity_status = 'FINISHED' THEN 0 ELSE 1 END,
             COALESCE(s.distance_to_finish_meters, 999999999) ASC,
             s.total_time_seconds ASC
         ) AS current_position,
         LAG(s.distance_to_finish_meters) OVER (
           ORDER BY
             CASE WHEN s.activity_status = 'FINISHED' THEN 0 ELSE 1 END,
             COALESCE(s.distance_to_finish_meters, 999999999) ASC,
             s.total_time_seconds ASC
         ) AS previous_distance_to_finish
       FROM activity_sessions s
       WHERE s.event_id = $1
         AND s.activity_status IN ('ACTIVE', 'PAUSED', 'FINISHED')
     )
     SELECT *,
       CASE
         WHEN current_position = 1 THEN 0
         WHEN previous_distance_to_finish IS NULL THEN NULL
         ELSE GREATEST(distance_to_finish_meters - previous_distance_to_finish, 0)
       END AS gap_to_previous_meters
     FROM ranked
     ORDER BY current_position ASC`,
    [eventId]
  );
  return result.rows;
};

module.exports = {
  findOpenSessionByUser,
  createActivitySession,
  findSessionById,
  saveActivityLocation,
  updateLiveSessionStats,
  finishActivitySession,
  pauseActivitySession,
  resumeActivitySession,
  markActiveSessionsAsDnfByEvent,
  getLiveRankingByEvent,
};
