const pool = require("../config/db");

const createResult = async (data) => {
  const result = await pool.query(
    `INSERT INTO event_results
     (
       event_id,
       auth_user_id,
       bib_number,
       category,
       total_time_seconds,
       pace_seconds_per_km,
       distance_km,
       result_status
     )
     VALUES ($1, $2, $3, $4, $5, $6, $7, 'DRAFT')
     RETURNING *`,
    [
      data.event_id,
      data.auth_user_id,
      data.bib_number || null,
      data.category || "GENERAL",
      data.total_time_seconds,
      data.pace_seconds_per_km || null,
      data.distance_km || null,
    ]
  );

  return result.rows[0];
};

const getResultsByEvent = async (eventId) => {
  const result = await pool.query(
    `SELECT *
     FROM event_results
     WHERE event_id = $1
       AND result_status = 'PUBLISHED'
     ORDER BY position ASC, total_time_seconds ASC`,
    [eventId]
  );

  return result.rows;
};

const getDraftResultsByEvent = async (eventId) => {
  const result = await pool.query(
    `SELECT *
     FROM event_results
     WHERE event_id = $1
     ORDER BY total_time_seconds ASC`,
    [eventId]
  );

  return result.rows;
};

const getMyResults = async (authUserId) => {
  const result = await pool.query(
    `SELECT *
     FROM event_results
     WHERE auth_user_id = $1
       AND result_status = 'PUBLISHED'
     ORDER BY published_at DESC`,
    [authUserId]
  );

  return result.rows;
};

const publishResultsByEvent = async (eventId) => {
  const client = await pool.connect();

  try {
    await client.query("BEGIN");

    const ranking = await client.query(
      `SELECT id
       FROM event_results
       WHERE event_id = $1
         AND result_status != 'CANCELLED'
       ORDER BY total_time_seconds ASC`,
      [eventId]
    );

    for (let i = 0; i < ranking.rows.length; i++) {
      await client.query(
        `UPDATE event_results
         SET position = $1,
             result_status = 'PUBLISHED',
             published_at = CURRENT_TIMESTAMP,
             updated_at = CURRENT_TIMESTAMP
         WHERE id = $2`,
        [i + 1, ranking.rows[i].id]
      );
    }

    await client.query("COMMIT");

    return {
      event_id: Number(eventId),
      total_published: ranking.rows.length,
    };
  } catch (error) {
    await client.query("ROLLBACK");
    throw error;
  } finally {
    client.release();
  }
};

const deleteResultById = async (resultId) => {
  const result = await pool.query(
    `DELETE FROM event_results
     WHERE id = $1
     RETURNING *`,
    [resultId]
  );

  return result.rows[0];
};

module.exports = {
  createResult,
  getResultsByEvent,
  getDraftResultsByEvent,
  getMyResults,
  publishResultsByEvent,
  deleteResultById,
};