const pool = require("../config/db");

const findRegistrationByEventAndUser = async (eventId, authUserId) => {
  const result = await pool.query(
    `SELECT * FROM event_registrations
     WHERE event_id = $1 AND auth_user_id = $2`,
    [eventId, authUserId]
  );
  return result.rows[0];
};

const createRegistration = async (eventId, authUserId) => {
  const result = await pool.query(
    `INSERT INTO event_registrations (event_id, auth_user_id)
     VALUES ($1, $2)
     RETURNING *`,
    [eventId, authUserId]
  );
  return result.rows[0];
};

const getMyRegistrations = async (authUserId) => {
  const result = await pool.query(
    `SELECT * FROM event_registrations
     WHERE auth_user_id = $1
     ORDER BY created_at DESC`,
    [authUserId]
  );
  return result.rows;
};

const countRegistrationsByEvents = async (eventIds) => {
  const result = await pool.query(
    `SELECT event_id, COUNT(*)::int AS total
     FROM event_registrations
     WHERE event_id = ANY($1::bigint[])
       AND registration_status = 'REGISTERED'
     GROUP BY event_id`,
    [eventIds]
  );

  return result.rows;
};

const getParticipantsByEventId = async (eventId) => {
  const result = await pool.query(
    `SELECT id, event_id, auth_user_id, registration_status, created_at
     FROM event_registrations
     WHERE event_id = $1
     ORDER BY created_at DESC`,
    [eventId]
  );

  return result.rows;
};

module.exports = {
  findRegistrationByEventAndUser,
  createRegistration,
  getMyRegistrations,
  countRegistrationsByEvents,
  getParticipantsByEventId,
};