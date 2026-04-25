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

module.exports = {
  findRegistrationByEventAndUser,
  createRegistration,
  getMyRegistrations,
};