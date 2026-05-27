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

const updateUserProfile = async (authUserId, data) => {
  const result = await pool.query(
    `UPDATE user_profile
     SET
       username = $1,
       first_name = $2,
       last_name = $3,
       phone = $4,
       birth_date = $5,
       gender = COALESCE($6, 'PREFER_NOT_TO_SAY'),
       city = $7,
       country = $8,
       bio = $9,
       profile_image_url = $10,
       profile_status = 'ACTIVE',
       updated_at = CURRENT_TIMESTAMP
     WHERE auth_user_id = $11
     RETURNING *`,
    [
      data.username,
      data.first_name,
      data.last_name,
      data.phone || null,
      data.birth_date || null,
      data.gender || "PREFER_NOT_TO_SAY",
      data.city || null,
      data.country || null,
      data.bio || null,
      data.profile_image_url || null,
      authUserId,
    ]
  );

  return result.rows[0];
};

module.exports = {
  createUserProfile,
  findProfileByAuthUserId,
  findProfileByUsername,
  updateUserProfile,
};