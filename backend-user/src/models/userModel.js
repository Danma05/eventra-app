const pool = require("../config/db");

const createUserProfile = async ({
  auth_user_id,
  username,
  first_name,
  last_name,
  phone,
  birth_date,
  gender,
  city,
  country,
  bio,
  profile_image_url,
}) => {
  const result = await pool.query(
    `INSERT INTO user_profile
    (auth_user_id, username, first_name, last_name, phone, birth_date, gender, city, country, bio, profile_image_url)
    VALUES ($1, $2, $3, $4, $5, $6, COALESCE($7, 'PREFER_NOT_TO_SAY'), $8, $9, $10, $11)
    RETURNING *`,
    [
      auth_user_id,
      username,
      first_name,
      last_name,
      phone || null,
      birth_date || null,
      gender || null,
      city || null,
      country || null,
      bio || null,
      profile_image_url || null,
    ]
  );

  return result.rows[0];
};

const findProfileByAuthUserId = async (authUserId) => {
  const result = await pool.query(
    `SELECT * FROM user_profile WHERE auth_user_id = $1`,
    [authUserId]
  );
  return result.rows[0];
};

const findProfileByUsername = async (username) => {
  const result = await pool.query(
    `SELECT * FROM user_profile WHERE username = $1`,
    [username]
  );
  return result.rows[0];
};

module.exports = {
  createUserProfile,
  findProfileByAuthUserId,
  findProfileByUsername,
};