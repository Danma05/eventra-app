const pool = require("../config/db");

const getAllEvents = async () => {
  const result = await pool.query(
    `SELECT * FROM events ORDER BY event_date ASC`
  );
  return result.rows;
};

const getEventById = async (id) => {
  const result = await pool.query(
    `SELECT * FROM events WHERE id = $1`,
    [id]
  );
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
}) => {
  const result = await pool.query(
    `INSERT INTO events
    (organizer_auth_user_id, title, description, event_date, location, capacity, image_url)
    VALUES ($1, $2, $3, $4, $5, $6, $7)
    RETURNING *`,
    [organizer_auth_user_id, title, description, event_date, location, capacity, image_url]
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
  const result = await pool.query(
    `DELETE FROM events WHERE id = $1 RETURNING *`,
    [id]
  );
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

module.exports = {
  getAllEvents,
  getEventById,
  createEvent,
  updateEvent,
  deleteEvent,
  getEventsByOrganizer,
};