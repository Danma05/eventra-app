import pool from "../config/db.js";

export const findUserByEmail = async (email) => {
  const result = await pool.query(
    `SELECT * FROM users_auth WHERE email = $1`,
    [email]
  );
  return result.rows[0];
};

export const createUser = async (email, passwordHash, accountType) => {
  const result = await pool.query(
    `INSERT INTO users_auth (email, password_hash, account_type)
     VALUES ($1, $2, $3)
     RETURNING id, email, account_type, is_verified, status, created_at`,
    [email, passwordHash, accountType]
  );

  return result.rows[0];
};

export const updateLastLogin = async (userId) => {
  await pool.query(
    `UPDATE users_auth
     SET last_login_at = CURRENT_TIMESTAMP,
         failed_login_attempts = 0,
         locked_until = NULL,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $1`,
    [userId]
  );
};

export const incrementFailedLoginAttempts = async (userId, attempts) => {
  await pool.query(
    `UPDATE users_auth
     SET failed_login_attempts = $2,
         updated_at = CURRENT_TIMESTAMP
     WHERE id = $1`,
    [userId, attempts]
  );
};