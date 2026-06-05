const pool = require("../config/db");

const getAllEvents = async () => {
  const result = await pool.query(`SELECT * FROM events ORDER BY event_date ASC`);
  return result.rows;
};

const getEventById = async (id) => {
  const result = await pool.query(`SELECT * FROM events WHERE id = $1`, [id]);
  return result.rows[0];
};

const createEvent = async ({
  organizer_auth_user_id,
  title,
  description,
  event_date,
  location,
  capacity,
  image_url,
  start_latitude,
  start_longitude,
  finish_latitude,
  finish_longitude,
}) => {
  const hasRoute =
    start_latitude !== undefined && start_latitude !== null &&
    start_longitude !== undefined && start_longitude !== null &&
    finish_latitude !== undefined && finish_latitude !== null &&
    finish_longitude !== undefined && finish_longitude !== null;

  const result = await pool.query(
    `INSERT INTO events
    (organizer_auth_user_id, title, description, event_date, location, capacity, image_url,
     start_latitude, start_longitude, finish_latitude, finish_longitude, race_status)
    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
    RETURNING *`,
    [
      organizer_auth_user_id,
      title,
      description,
      event_date,
      location,
      capacity,
      image_url,
      start_latitude ?? null,
      start_longitude ?? null,
      finish_latitude ?? null,
      finish_longitude ?? null,
      hasRoute ? "READY" : "CREATED",
    ]
  );

  return result.rows[0];
};

const updateEvent = async (
  id,
  { title, description, event_date, location, capacity, status, image_url }
) => {
  const result = await pool.query(
    `UPDATE events
     SET title = $1,
         description = $2,
         event_date = $3,
         location = $4,
         capacity = $5,
         status = $6,
         image_url = $7,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $8
     RETURNING *`,
    [title, description, event_date, location, capacity, status, image_url, id]
  );

  return result.rows[0];
};

const deleteEvent = async (id) => {
  const result = await pool.query(`DELETE FROM events WHERE id = $1 RETURNING *`, [id]);
  return result.rows[0];
};

const getEventsByOrganizer = async (organizerAuthUserId) => {
  const result = await pool.query(
    `SELECT *
     FROM events
     WHERE organizer_auth_user_id = $1
     ORDER BY created_at DESC`,
    [organizerAuthUserId]
  );

  return result.rows;
};

const updateEventRoute = async ({
  id,
  start_latitude,
  start_longitude,
  finish_latitude,
  finish_longitude,
}) => {
  const result = await pool.query(
    `UPDATE events
     SET start_latitude = $1,
         start_longitude = $2,
         finish_latitude = $3,
         finish_longitude = $4,
         race_status = CASE
           WHEN race_status IN ('STARTED', 'PAUSED', 'FINISHED', 'CANCELLED') THEN race_status
           ELSE 'READY'
         END,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $5
     RETURNING *`,
    [start_latitude, start_longitude, finish_latitude, finish_longitude, id]
  );

  return result.rows[0];
};

const updateRaceStatus = async (id, raceStatus) => {
  const timestampColumn = {
    STARTED: "race_started_at",
    PAUSED: "race_paused_at",
    READY: "race_resumed_at",
    FINISHED: "race_finished_at",
    CANCELLED: "race_finished_at",
  }[raceStatus];

  const sql = timestampColumn
    ? `UPDATE events
       SET race_status = $1,
           ${timestampColumn} = CURRENT_TIMESTAMP,
           updated_at = CURRENT_TIMESTAMP
       WHERE id = $2
       RETURNING *`
    : `UPDATE events
       SET race_status = $1,
           updated_at = CURRENT_TIMESTAMP
       WHERE id = $2
       RETURNING *`;

  const result = await pool.query(sql, [raceStatus, id]);
  return result.rows[0];
};

module.exports = {
  getAllEvents,
  getEventById,
  createEvent,
  updateEvent,
  deleteEvent,
  getEventsByOrganizer,
  updateEventRoute,
  updateRaceStatus,
};
